package com.mykolamelnyk.JobScraperApp.util.scratching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechstarsFilterBuilder {

    private final Map<String, Object> filter = new HashMap<>();

    public TechstarsFilterBuilder withSearch(String q) {
        if (q != null && !q.isBlank()) {
            filter.put("q", q);
        }
        return this;
    }

    public TechstarsFilterBuilder withJobFunctions(List<String> jobFunctions) {
        if (jobFunctions != null && !jobFunctions.isEmpty()) {
            filter.put("job_functions", jobFunctions);
        }
        return this;
    }

    public TechstarsFilterBuilder withSeniority(List<String> seniority) {
        if (seniority != null && !seniority.isEmpty()) {
            filter.put("seniority", seniority);
        }
        return this;
    }

    public TechstarsFilterBuilder withCompensation(String currency, String period, Integer amountMinCents, Boolean offersEquity) {
        if (currency != null) {
            filter.put("compensation_currency", currency);
        }
        if (period != null) {
            filter.put("compensation_period", period);
        }
        if (amountMinCents != null) {
            filter.put("compensation_amount_min_cents", amountMinCents.toString());
        }
        if (offersEquity != null) {
            filter.put("compensation_offers_equity", offersEquity);
        }
        return this;
    }

    public TechstarsFilterBuilder withIndustryTags(List<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            filter.put("organization.industry_tags", tags);
        }
        return this;
    }

    public TechstarsFilterBuilder withOrganizationStage(List<String> stages) {
        if (stages != null && !stages.isEmpty()) {
            filter.put("organization.stage", stages);
        }
        return this;
    }

    public TechstarsFilterBuilder withHeadCount(List<String> headCount) {
        if (headCount != null && !headCount.isEmpty()) {
            filter.put("organization.head_count", headCount);
        }
        return this;
    }

    public TechstarsFilterBuilder withTopics(List<String> topics) {
        if (topics != null && !topics.isEmpty()) {
            filter.put("organization.topics.topics", topics);
        }
        return this;
    }

    public TechstarsFilterBuilder withOrganizationIds(List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            filter.put("organization.id", ids);
        }
        return this;
    }

    public TechstarsFilterBuilder withLocationOptions(List<String> options) {
        if (options != null && !options.isEmpty()) {
            filter.put("searchable_location_option", options);
        }
        return this;
    }

    public TechstarsFilterBuilder withSearchableLocations(List<String> locations) {
        if (locations != null && !locations.isEmpty()) {
            filter.put("searchable_locations", locations);
        }
        return this;
    }

    public Map<String, Object> build() {
        return filter;
    }
}