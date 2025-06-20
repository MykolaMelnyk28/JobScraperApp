package com.mykolamelnyk.JobScraperApp.util.scratching;

import com.mykolamelnyk.JobScraperApp.model.entity.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.mykolamelnyk.JobScraperApp.util.scratching.HtmlIdentifiers.JOB_LIST_ITEM_SELECTOR;
import static com.mykolamelnyk.JobScraperApp.util.scratching.HtmlIdentifiers.JOB_LIST_TOTAL_SIZE_SELECTOR;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobScratcherImpl {

    private final Connection connection;
    private final ElementMapper<Job> jobElementMapper;
    private final ElementMapper<Integer> integerElementMapper;

//    public long getTotalSize() throws IOException {
//        Document document = getJobsPageDocument();
//        Element totalSizeElm = document.selectFirst(JOB_LIST_TOTAL_SIZE_SELECTOR);
//        Integer totalSizeInt = integerElementMapper.map(totalSizeElm);
//        if (totalSizeInt != null) {
//            return totalSizeInt.longValue();
//        }
//        Elements elements = document.select(JOB_LIST_ITEM_SELECTOR);
//        return elements.size();
//    }

    public Page<Job> fetchAll(Pageable pageable) {
        try {
            Document document = getJobsPageDocument();

            Elements elements = document.select(JOB_LIST_ITEM_SELECTOR);
            Element totalSizeElm = document.selectFirst(JOB_LIST_TOTAL_SIZE_SELECTOR);

            Long totalSize = null;
            Integer totalSizeInt = integerElementMapper.map(totalSizeElm);
            if (totalSizeInt != null) {
                totalSize = totalSizeInt.longValue();
            }

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<CompletableFuture<Job>> futures;
                if (pageable != null) {
                    futures = elements.stream()
                        .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(elm -> CompletableFuture.supplyAsync(() -> jobElementMapper.map(elm), executor))
                        .toList();
                } else {
                    futures = elements.stream()
                        .map(elm -> CompletableFuture.supplyAsync(() -> jobElementMapper.map(elm), executor))
                        .toList();
                }

                CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
                allDone.join();

                List<Job> jobs = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                if (totalSize == null) {
                    totalSize = (long) elements.size();
                }

                return pageable == null ? new PageImpl<>(jobs) : new PageImpl<>(jobs, pageable, totalSize);
            }

        } catch (IOException e) {
            Thread.currentThread().interrupt();
            String message = "❌ Failed to fetch job listings";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    private Document getJobsPageDocument() throws IOException {
        Connection con = connection.newRequest(connection.request().url() + "/jobs");
        return con.get();
    }

}