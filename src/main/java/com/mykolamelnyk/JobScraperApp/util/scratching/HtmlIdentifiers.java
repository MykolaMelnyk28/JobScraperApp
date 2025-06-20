package com.mykolamelnyk.JobScraperApp;

public interface HtmlIdentifiers {

    String JOB_LIST_ITEM_SELECTOR = "div[data-testid=job-list-item]";
    String JOB_LIST_ITEM_LINK_ATTRIBUTE = "a[data-testid=job-title-link]";

    String JOB_CONTENT_BLOCK_SELECTOR = "div[data-testid=content]";

    String JOB_POSITION_NAME_SELECTOR = "div[data-testid=content] h2.sc-beqWaB.jqWDOR";
    String JOB_ORGANIZATION_URL_SELECTOR = "div[data-testid=content] a[data-testid=button]";
    String JOB_LOGO_LINK_SELECTOR = "div[data-testid=content] [data-testid=image]";
    String JOB_ORGANIZATION_TITLE_SELECTOR = "div[data-testid=content] p.sc-beqWaB.bpXRKw";

    String JOB_SUB_GROUP_ONE_SELECTOR = "div[data-testid=content] div.sc-beqWaB.sc-gueYoa.kDyJCZ.MYFxR div.sc-beqWaB.sc-gueYoa.dmdAKU.MYFxR";

    //String JOB_LABOR_FUNCTION_SELECTOR = "div[data-testid=content] div.sc-beqWaB.sc-gueYoa.kDyJCZ.MYFxR div.sc-beqWaB.sc-gueYoa.dmdAKU.MYFxR div.sc-beqWaB.bpXRKw:nth-child(1)";
    //String JOB_LOCATION_SELECTOR = "div[data-testid=content] div.sc-beqWaB.sc-gueYoa.kDyJCZ.MYFxR div.sc-beqWaB.sc-gueYoa.dmdAKU.MYFxR div.sc-beqWaB.bpXRKw:nth-child(2)";

    String JOB_DESCRIPTION_SELECTOR = "div[data-testid=content] div[data-testid=careerPage]";
    String JOB_TAG_SELECTOR = "div[data-testid=tag]";

}
