package com.mykolamelnyk.JobScraperApp.util.scratching;

import org.jsoup.select.Elements;

import java.util.stream.Stream;

@FunctionalInterface
public interface ElementsMapper<T> {
    Stream<T> map(Elements elements);
}