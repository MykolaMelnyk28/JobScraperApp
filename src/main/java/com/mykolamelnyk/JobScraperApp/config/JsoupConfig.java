package com.mykolamelnyk.JobScraperApp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykolamelnyk.JobScraperApp.model.dto.JobDto;
import com.mykolamelnyk.JobScraperApp.model.entity.Tag;
import com.mykolamelnyk.JobScraperApp.util.scratching.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

@Configuration
public class JsoupConfig {

    @Value("${scratcher.base-url}")
    private String baseUrl;

    @Bean
    public ElementMapper<JobDto> jobElementMapper(
        ElementMapper<String> textElementMapper,
        ElementMapper<String> htmlElementMapper,
        ElementMapper<LocalDate> postedDateElementMapper,
        ElementsMapper<Tag> listBodyTagMapper
    ) {
        return new JobElementMapper(baseUrl, textElementMapper, htmlElementMapper, postedDateElementMapper, listBodyTagMapper);
    }

    @Bean
    public ElementMapper<String> textElementMapper() {
        return elm -> elm == null || elm.text().isBlank() ? null : elm.text().trim();
    }

    @Bean
    public ElementMapper<Tag> tagElementMapper() {
        return elm -> elm == null || elm.text().isBlank() ? null : new Tag(null, elm.text().trim());
    }

    @Bean
    public ElementMapper<String> htmlElementMapper() {
        return elm -> elm == null || elm.html().isBlank() ? null : elm.html().trim();
    }

    @Bean
    public ElementMapper<LocalDate> postedDateElementMapper(ElementMapper<String> textElementMapper) {
        return elm -> {
            String dateStr = textElementMapper.map(elm);
            DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("'Posted on 'MMM d, yyyy", Locale.ENGLISH);
            return dateStr == null ? null : LocalDate.parse(dateStr, formatter);
        };
    }

    @Bean
    public ElementMapper<Integer> integerElementMapper(ElementMapper<String> textElementMapper) {
        return elm -> {
            String numStr = textElementMapper.map(elm);
            if (numStr == null) return null;
            numStr = numStr.replaceAll("(\\s+|,|\\.|_)", "").trim();
            return numStr.isEmpty() ? null : Integer.parseInt(numStr);
        };
    }

    @Bean
    public ElementsMapper<String> listBodyTextMapper(ElementMapper<String> textElementMapper) {
        return elements -> {
            if (elements == null) return Stream.empty();
            return elements.stream()
                .map(textElementMapper::map)
                .filter(Objects::nonNull);
        };
    }

    @Bean
    public ElementsMapper<Tag> listBodyTagMapper(ElementMapper<Tag> tagElementMapper) {
        return elements -> {
            if (elements == null) return Stream.empty();
            return elements.stream()
                .map(tagElementMapper::map)
                .filter(Objects::nonNull);
        };
    }

}
