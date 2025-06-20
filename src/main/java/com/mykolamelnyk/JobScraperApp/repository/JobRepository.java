package com.mykolamelnyk.JobScraperApp.repository;

import com.mykolamelnyk.JobScraperApp.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    List<Job> findAllByUrlIn(Collection<String> urls);

    @Query("SELECT DISTINCT j.organizationTitle FROM Job j")
    List<String> findAllOrganizationTitles();

    @Query("SELECT DISTINCT j.laborFunction FROM Job j")
    List<String> findAllLaborFunctions();

    @Query("SELECT DISTINCT j.location FROM Job j")
    List<String> findAllLocations();

}
