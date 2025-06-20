package com.mykolamelnyk.JobScraperApp.model.dto;

import com.mykolamelnyk.JobScraperApp.model.entity.Job;
import com.mykolamelnyk.JobScraperApp.model.entity.Tag;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record JobDto(
    String positionName,
    String organizationUrl,
    String organizationTitle,
    String logoUrl,
    String laborFunction,
    String location,
    List<Tag> tags,
    String description,
    Instant postedDatetime,
    String url
) {

    public Job toEntity() {
        return new Job(
            null,
            positionName,
            organizationUrl,
            organizationTitle,
            logoUrl,
            laborFunction,
            location,
            tags,
            description,
            postedDatetime,
            url
        );
    }

}
