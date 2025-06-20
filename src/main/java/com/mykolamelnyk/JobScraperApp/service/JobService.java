package com.mykolamelnyk.JobScraperApp.service;

import com.mykolamelnyk.JobScraperApp.model.entity.Job;
import com.mykolamelnyk.JobScraperApp.model.dto.JobDto;
import com.mykolamelnyk.JobScraperApp.model.dto.JobFilter;
import com.mykolamelnyk.JobScraperApp.repository.JobRepository;
import com.mykolamelnyk.JobScraperApp.util.scratching.JobScratcher;
import com.mykolamelnyk.JobScraperApp.util.scratching.JobSpecificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobScratcher jobScratcher;
    private final TagService tagService;

    @Transactional
    public List<Job> ensureJobs(Collection<Job> collection) {
        List<String> urls = collection.stream()
            .map(Job::getUrl)
            .toList();

        List<Job> existingJobs = jobRepository.findAllByUrlIn(urls);

        Map<String, Job> existingJobMap = existingJobs.stream()
            .collect(Collectors.toMap(Job::getUrl, Function.identity()));

        List<Job> toSave = new ArrayList<>();

        for (Job incoming : collection) {
            incoming.setTags(tagService.ensureTags(incoming.getTags()));
            Job existing = existingJobMap.get(incoming.getUrl());
            if (existing != null) {
                existing.setPositionName(incoming.getPositionName());
                existing.setOrganizationUrl(incoming.getOrganizationUrl());
                existing.setLogoUrl(incoming.getLogoUrl());
                existing.setOrganizationTitle(incoming.getOrganizationTitle());
                existing.setLocation(incoming.getLocation());
                existing.setTags(incoming.getTags());
                existing.setDescription(incoming.getDescription());
                existing.setPostedDatetime(incoming.getPostedDatetime());

                toSave.add(existing);
            } else {
                toSave.add(incoming);
            }
        }

        return jobRepository.saveAll(toSave);
    }

    public int getSynchronizeTotalSize() {
        try {
            return jobScratcher.getTotalSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<Job> synchronize(Map<String, Object> filter, Pageable pageable) {
        try {
            List<Job> page = jobScratcher.fetchAll(filter, pageable)
                .map(JobDto::toEntity)
                .toList();
            return ensureJobs(page);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Job> getAll(JobFilter filter, Pageable pageable) {
        Specification<Job> specification = JobSpecificationFactory.create(filter);
        return jobRepository.findAll(specification, pageable);
    }

    public List<String> getAllOrganizationTitles() {
        return jobRepository.findAllOrganizationTitles();
    }

    public List<String> getAllLaborFunctions() {
        return jobRepository.findAllLaborFunctions();
    }

    public List<String> getAllLocations() {
        return jobRepository.findAllLocations();
    }

}
