package com.mykolamelnyk.JobScraperApp.util.scratching;

import com.mykolamelnyk.JobScraperApp.model.dto.JobDto;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

public interface JobScratcher {

    int getTotalSize() throws IOException;
    Stream<JobDto> fetchAll(Map<String, Object> filter, Pageable pageable);

}
