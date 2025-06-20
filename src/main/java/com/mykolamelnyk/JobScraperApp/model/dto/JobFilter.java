package com.mykolamelnyk.JobScraperApp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobFilter {

    private String search;
    private List<String> organizations;
    private List<String> laborFunctions;
    private List<String> locations;
    private List<String> tags;
    private LocalDate fromPostedDate;
    private LocalDate toPostedDate;
    private String sort;

}
