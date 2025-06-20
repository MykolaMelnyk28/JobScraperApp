package com.mykolamelnyk.JobScraperApp.util.scratching;

import com.mykolamelnyk.JobScraperApp.model.dto.JobDto;
import com.mykolamelnyk.JobScraperApp.model.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mykolamelnyk.JobScraperApp.util.scratching.HtmlIdentifiers.*;

@Slf4j
@RequiredArgsConstructor
public class JobElementMapper implements ElementMapper<JobDto> {

    private final String baseUrl;
    private final ElementMapper<String> textElementMapper;
    private final ElementMapper<String> htmlElementMapper;
    private final ElementMapper<LocalDate> postedDateElementMapper;
    private final ElementsMapper<Tag> listTagElementMapper;

    @Override
    public JobDto map(Element element) {
        String jobUrl = null;
        try {
            JobDto.JobDtoBuilder builder = JobDto.builder();

            Elements tagElements = element.select(JOB_TAG_SELECTOR);
            builder.tags(listTagElementMapper.map(tagElements)
                .collect(Collectors.toList()));

            Element itemLink = element.selectFirst(JOB_LIST_ITEM_LINK_ATTRIBUTE);

            jobUrl = getAttributeValue(itemLink, "href")
                .orElseThrow(() -> {
                    String message = "Job link href not found";
                    log.error(message);
                    return new IllegalStateException(message);
                });
            if (jobUrl.startsWith("http") || jobUrl.startsWith("https")) {
                if (!jobUrl.startsWith(baseUrl)) {
                    return null;
                }
            } else {
                jobUrl = baseUrl + jobUrl;
            }

            if (jobUrl.equals("https://jobs.techstars.com/companies/climatize/jobs/52915075-ai-ml-engineering-lead#content")) {
                System.out.println("found");
            }

            log.info("🔍 Fetching: " + jobUrl);

            builder.url(jobUrl);

            Document document = fetchWithRetry(jobUrl, 3);

            buildFromDetailPage(builder, document);

            return builder.build();
        } catch (IOException e) {
            String message = "❌ Error loading job detail page";
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    private void buildFromDetailPage(JobDto.JobDtoBuilder builder, Document document) {
        Element logoElm = document.selectFirst(JOB_LOGO_LINK_SELECTOR);
        getAttributeValue(logoElm, "src").ifPresent(builder::logoUrl);

        Element positionNameElm = document.selectFirst(JOB_POSITION_NAME_SELECTOR);
        builder.positionName(textElementMapper.map(positionNameElm));

        Element organizationUrlElm = document.selectFirst(JOB_ORGANIZATION_URL_SELECTOR);
        getAttributeValue(organizationUrlElm, "href").ifPresent(builder::organizationUrl);

        Element organizationTitleElm = document.selectFirst(JOB_ORGANIZATION_TITLE_SELECTOR);
        builder.organizationTitle(textElementMapper.map(organizationTitleElm));

        Element subGroupOneElement = document.selectFirst(JOB_SUB_GROUP_ONE_SELECTOR);
        if (subGroupOneElement != null) {
            Elements children = subGroupOneElement.children();

            if (!children.isEmpty()) {
                String laborFunction = textElementMapper.map(children.getFirst());
                builder.laborFunction(laborFunction);
                LocalDate date = postedDateElementMapper.map(children.getLast());
                if (date != null) {
                    Instant instant = date.atStartOfDay(ZoneOffset.UTC).toInstant();
                    builder.postedDatetime(instant);
                }

                if (children.size() >= 3) {
                    builder.location(textElementMapper.map(children.get(1)));
                }
            }
        }

        Element descriptionElement = document.selectFirst(JOB_DESCRIPTION_SELECTOR);
        builder.description(htmlElementMapper.map(descriptionElement));
    }

    private Optional<String> getAttributeValue(Element element, String name) {
        return Optional.ofNullable(element)
            .map(el -> el.attr(name))
            .map(String::trim)
            .filter(s -> !s.isEmpty());
    }

    Document fetchWithRetry(String url, int retries) throws IOException {
        final int timeout = 30_000;
        for (int i = 0; i < retries; i++) {
            try {
                return Jsoup.connect(url).timeout(timeout).get();
            } catch (SocketTimeoutException e) {
                log.warn("⏳ Timeout when loading {}, attempt {}/{}", url, i + 1, retries);
                try {
                    Thread.sleep(1000L * (i + 1)); // backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        throw new RuntimeException("❌ Failed to load job after " + retries + " attempts: " + url);
    }
}