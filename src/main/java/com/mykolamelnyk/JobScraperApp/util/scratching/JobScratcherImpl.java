package com.mykolamelnyk.JobScraperApp.util.scratching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykolamelnyk.JobScraperApp.model.dto.JobDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mykolamelnyk.JobScraperApp.util.scratching.HtmlIdentifiers.*;

@Slf4j
@RequiredArgsConstructor
public class JobScratcherImpl implements JobScratcher {

    private static final int BATCH_SIZE = 100;

    private final String baseUrl;
    private final ObjectMapper objectMapper;
    private final ElementMapper<JobDto> jobElementMapper;
    private final ElementMapper<Integer> integerElementMapper;

    @Override
    public int getTotalSize() throws IOException {
        String url = buildUrlWithFilter(null);
        Document document = Jsoup.connect(url).get();
        Elements elements = document.select(JOB_LIST_ITEM_SELECTOR);
        return Optional.ofNullable(document.selectFirst(JOB_LIST_TOTAL_SIZE_SELECTOR))
            .map(integerElementMapper::map)
            .orElse(elements.size());
    }

    @Override
    public Stream<JobDto> fetchAll(Map<String, Object> filter, Pageable pageable) {
        try {
            String url = buildUrlWithFilter(filter);
            int totalSize = getTotalSize();
            Document document = null;
            if (totalSize <= 0) {
                document = Jsoup.connect(url).get();
            } else {
                document = getJobsPageDocument(filter, pageable);
            }

            Elements elements = document.select(JOB_LIST_ITEM_SELECTOR);

            log.info("📄 Total jobs visible: {}", totalSize);

            long offset = pageable == null ? 0 : pageable.getOffset();
            long limit = pageable == null ? totalSize : pageable.getPageSize();

            List<Element> filteredElements = elements.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

            List<JobDto> result = new ArrayList<>();

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < filteredElements.size(); i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, filteredElements.size());
                    List<Element> batch = filteredElements.subList(i, end);

                    CompletableFuture<List<JobDto>> batchFuture = CompletableFuture.supplyAsync(() -> {
                        return batch.stream()
                            .map(jobElementMapper::map)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    }, executor);

                    result.addAll(batchFuture.join());
                }
            }

            return result.stream();

        } catch (IOException e) {
            Thread.currentThread().interrupt();
            log.error("❌ Failed to fetch job listings", e);
            throw new RuntimeException("❌ Failed to fetch job listings", e);
        }
    }

    private Document getJobsPageDocument(Map<String, Object> filter, Pageable pageable) throws IOException {
        System.setProperty("webdriver.chrome.driver", "chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);

        try {
            String url = buildUrlWithFilter(filter);
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            By loadMoreSelector = By.cssSelector(JOB_LIST_LOAD_MORE_BUTTON_SELECTOR);
            By totalSizeSelector = By.cssSelector(JOB_LIST_TOTAL_SIZE_SELECTOR);

            int totalSize = wait.until(ExpectedConditions.presenceOfElementLocated(totalSizeSelector))
                .getText()
                .replaceAll("[^\\d]", "")
                .trim()
                .isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(
                wait.until(ExpectedConditions.presenceOfElementLocated(totalSizeSelector))
                    .getText().replaceAll("[^\\d]", ""));

            int targetCount = pageable == null
                ? totalSize
                : (int) pageable.getOffset() + pageable.getPageSize();

            if (targetCount <= 20) {
                return Jsoup.parse(driver.getPageSource());
            }

            WebElement loadMoreBtn = wait.until(ExpectedConditions.presenceOfElementLocated(loadMoreSelector));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loadMoreBtn);
            wait.until(ExpectedConditions.elementToBeClickable(loadMoreBtn)).click();
            log.info("✅ 'Load more' button clicked.");
            Thread.sleep(1500);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            int loadedCount = 0;
            int attempts = 0;

            while (loadedCount < targetCount && attempts < 30) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(2000);
                loadedCount = driver.findElements(By.cssSelector(JOB_LIST_ITEM_SELECTOR)).size();
                log.info("⬇️ Loaded jobs: {}/{}", loadedCount, targetCount);
                attempts++;
            }

            return Jsoup.parse(driver.getPageSource());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while scrolling", e);
        } finally {
            driver.quit();
        }
    }

    private String buildUrlWithFilter(Map<String, Object> filter) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder(baseUrl).append("/jobs");
        if (filter != null && !filter.isEmpty()) {
            String json = objectMapper.writeValueAsString(filter);
            String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            builder.append("?filter=").append(encoded);
        }
        return builder.toString();
    }
}