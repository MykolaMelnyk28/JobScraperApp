package com.mykolamelnyk.JobScraperApp.util.scratching;

public final class HtmlIdentifiers {
    private HtmlIdentifiers() {}

    public static final String JOB_LIST_ITEM_SELECTOR = "div[data-testid=job-list-item]";
    public static final String JOB_LIST_ITEM_LINK_ATTRIBUTE = "a[data-testid=job-title-link]";
    public static final String JOB_LIST_TOTAL_SIZE_SELECTOR = "#content div[data-testid=header] b";
    public static final String JOB_POSITION_NAME_SELECTOR = "div[data-testid=content] h2.sc-beqWaB.jqWDOR";
    public static final String JOB_ORGANIZATION_URL_SELECTOR = "div[data-testid=content] a[data-testid=button]";
    public static final String JOB_LOGO_LINK_SELECTOR = "div[data-testid=content] [data-testid=image]";
    public static final String JOB_ORGANIZATION_TITLE_SELECTOR = "div[data-testid=content] p.sc-beqWaB.bpXRKw";
    public static final String JOB_SUB_GROUP_ONE_SELECTOR = "div[data-testid=content] div.sc-beqWaB.sc-gueYoa.kDyJCZ.MYFxR div.sc-beqWaB.sc-gueYoa.dmdAKU.MYFxR";
    public static final String JOB_DESCRIPTION_SELECTOR = "div[data-testid=content] div[data-testid=careerPage]";
    public static final String JOB_TAG_SELECTOR = "div[data-testid=tag]";
    public static final String JOB_LIST_LOAD_MORE_BUTTON_SELECTOR = "#content div[data-testid=container] div[data-testid=results-list-job] div[data-testid=footer] button[data-testid=load-more]";

}
