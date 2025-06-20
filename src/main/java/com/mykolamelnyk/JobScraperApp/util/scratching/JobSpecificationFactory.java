package com.mykolamelnyk.JobScraperApp.util.scratching;

import com.mykolamelnyk.JobScraperApp.model.entity.Job;
import com.mykolamelnyk.JobScraperApp.model.dto.JobFilter;
import com.mykolamelnyk.JobScraperApp.model.entity.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class JobSpecificationFactory {

    public static Specification<Job> create(JobFilter filter) {
        if (filter == null) {
            return Specification.allOf();
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String searchLower = filter.getSearch().toLowerCase().trim();
                String[] words = searchLower.split("\\W+");

                List<Predicate> searchPredicates = new ArrayList<>();
                for (String word : words) {
                    if (!word.isBlank()) {
                        String pattern = "%" + word + "%";
                        Predicate byPosition = cb.like(cb.lower(root.get("positionName")), pattern);
                        Predicate byOrganization = cb.like(cb.lower(root.get("organizationTitle")), pattern);
                        searchPredicates.add(cb.or(byPosition, byOrganization));
                    }
                }

                if (!searchPredicates.isEmpty()) {
                    predicates.add(cb.and(searchPredicates.toArray(Predicate[]::new)));
                }
            }

            if (filter.getOrganizations() != null && !filter.getOrganizations().isEmpty()) {
                predicates.add(root.get("organizationTitle").in(filter.getOrganizations()));
            }

            if (filter.getLaborFunctions() != null && !filter.getLaborFunctions().isEmpty()) {
                predicates.add(root.get("laborFunction").in(filter.getLaborFunctions()));
            }

            if (filter.getLocations() != null && !filter.getLocations().isEmpty()) {
                predicates.add(root.get("location").in(filter.getLocations()));
            }

            if (filter.getTags() != null && !filter.getTags().isEmpty()) {
                Join<Job, Tag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(tagJoin.get("name").in(filter.getTags()));
                query.distinct(true);
            }

            if (filter.getFromPostedDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("postedDatetime"),
                    filter.getFromPostedDate().atStartOfDay().toInstant(ZoneOffset.UTC)
                ));
            }

            if (filter.getToPostedDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("postedDatetime"),
                    filter.getToPostedDate().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC)
                ));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}