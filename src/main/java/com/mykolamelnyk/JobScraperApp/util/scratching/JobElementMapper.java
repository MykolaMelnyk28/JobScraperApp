package com.mykolamelnyk.JobScraperApp.service;

import com.mykolamelnyk.JobScraperApp.model.entity.Job;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static com.mykolamelnyk.JobScraperApp.HtmlIdentifiers.*;

public class JobElementMapper implements ElementMapper<Job> {

    private static final DateTimeFormatter POSTED_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("'Posted on 'MMM dd, yyyy", Locale.ENGLISH);

    private final String baseUrl;
    private final ElementMapper<String> textElementMapper;
    private final ElementMapper<String> htmlElementMapper;
    private final ElementMapper<LocalDate> postedDateElementMapper;
    private final ElementsMapper<String> listTextElementMapper;

    public JobElementMapper(String baseUrl) {
        this.baseUrl = baseUrl;
        this.textElementMapper = ElementMapperFactory.createTextElementMapper();
        this.htmlElementMapper = ElementMapperFactory.createHtmlElementMapper();
        this.postedDateElementMapper = ElementMapperFactory.createLocalDateMapper(POSTED_DATE_FORMATTER);
        this.listTextElementMapper = ElementMapperFactory.createListBodyTextMapper();
    }

    @Override
    public Job map(Element element) {
        try {
            Elements tagElements = element.select(JOB_TAG_SELECTOR);
            Element itemLink = element.selectFirst(JOB_LIST_ITEM_LINK_ATTRIBUTE);

            String href = getAttributeValue(itemLink, "href")
                .orElseThrow(() -> new IllegalStateException("Job link href not found"));

            String jobUrl = baseUrl + href;
            System.out.println("🔍 Fetching: " + jobUrl);

            Document document = Jsoup.connect(jobUrl).get();
            Job.JobBuilder builder = Job.builder();

            getAttributeValue(document.selectFirst(JOB_LOGO_LINK_SELECTOR), "src").ifPresent(builder::logoUrl);
            builder.positionName(textElementMapper.map(document.selectFirst(JOB_POSITION_NAME_SELECTOR)));
            getAttributeValue(document.selectFirst(JOB_ORGANIZATION_URL_SELECTOR), "href").ifPresent(builder::organizationUrl);
            builder.organizationTitle(textElementMapper.map(document.selectFirst(JOB_ORGANIZATION_TITLE_SELECTOR)));

            Element subGroupOneElement = document.selectFirst(JOB_SUB_GROUP_ONE_SELECTOR);
            if (subGroupOneElement != null) {
                Elements children = subGroupOneElement.children();
                if (children.size() >= 3) {
                    builder.laborFunction(textElementMapper.map(children.get(0)));
                    builder.location(textElementMapper.map(children.get(1)));
                    builder.postedDatetime(postedDateElementMapper.map(children.getLast()));
                }
            }

            builder.description(htmlElementMapper.map(document.selectFirst(JOB_DESCRIPTION_SELECTOR)));
            builder.tags(listTextElementMapper.map(tagElements));

            return builder.build();

        } catch (IOException e) {
            throw new RuntimeException("❌ Error loading job detail page", e);
        }
    }

    private Optional<String> getAttributeValue(Element element, String name) {
        return Optional.ofNullable(element)
            .map(el -> el.attr(name))
            .map(String::trim)
            .filter(s -> !s.isEmpty());
    }
}