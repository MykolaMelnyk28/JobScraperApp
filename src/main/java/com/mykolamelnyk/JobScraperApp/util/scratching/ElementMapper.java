package com.mykolamelnyk.JobScraperApp.util.scratching;

import org.jsoup.nodes.Element;

@FunctionalInterface
public interface ElementMapper<T> {
    T map(Element element);
}