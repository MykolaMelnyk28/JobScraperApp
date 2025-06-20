package com.mykolamelnyk.JobScraperApp.repository;

import com.mykolamelnyk.JobScraperApp.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByNameIn(Collection<String> names);

}
