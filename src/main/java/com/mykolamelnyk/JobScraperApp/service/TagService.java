package com.mykolamelnyk.JobScraperApp.service;

import com.mykolamelnyk.JobScraperApp.model.entity.Tag;
import com.mykolamelnyk.JobScraperApp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Transactional
    public List<Tag> ensureTags(Collection<Tag> iterable) {
        return ensureTagsByNames(iterable.stream().map(Tag::getName).toList());
    }

    @Transactional
    public List<Tag> ensureTagsByNames(Collection<String> names) {
        List<Tag> foundTags = tagRepository.findAllByNameIn(names);

        Set<String> foundNames = foundTags.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());

        List<String> notFoundNames = new ArrayList<>(names);
        notFoundNames.removeAll(foundNames);

        List<Tag> notFoundTags = notFoundNames.stream()
            .map(name -> new Tag(null, name))
            .toList();

        if (!notFoundTags.isEmpty()) {
            List<Tag> savedTags = tagRepository.saveAll(notFoundTags);
            foundTags.addAll(savedTags);
        }

        return foundTags;
    }

}
