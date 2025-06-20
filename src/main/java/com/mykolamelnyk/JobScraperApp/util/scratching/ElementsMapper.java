package com.mykolamelnyk.JobScraperApp.service;

import org.jsoup.select.Elements;

import java.util.List;

@FunctionalInterface
public interface ElementsMapper<T> {
    List<T> map(Elements elements);
}