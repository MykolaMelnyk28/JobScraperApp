package com.mykolamelnyk.JobScraperApp.service;

import org.jsoup.nodes.Element;

@FunctionalInterface
public interface ElementMapper<T> {
    T map(Element element);
}