package com.mykolamelnyk.JobScraperApp.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "jobs",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_jobs_url", columnNames = "url")
    }
)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_seq")
    @SequenceGenerator(name = "job_seq", sequenceName = "job_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String positionName;

    @Column(nullable = false, length = 2048)
    private String organizationUrl;

    @Column(nullable = false)
    private String organizationTitle;

    @Column(length = 2048)
    private String logoUrl;

    private String laborFunction;

    private String location;

    @ManyToMany
    @JoinTable(
        name = "job_tags",
        joinColumns = @JoinColumn(name = "job_id", foreignKey = @ForeignKey(name = "fk_job_tags_jobs_id")),
        inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_job_tags_tags_id"))
    )
    private List<Tag> tags;

    @Column(columnDefinition = "TEXT NOT NULL ")
    private String description;

    @Column(nullable = false)
    private Instant postedDatetime;

    @Column(nullable = false, length = 2048)
    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        Job job = (Job) o;
        return Objects.equals(url, job.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "Job{" +
               "id=" + id +
               ", positionName='" + positionName + '\'' +
               ", organizationUrl='" + organizationUrl + '\'' +
               ", organizationTitle='" + organizationTitle + '\'' +
               ", logoUrl='" + logoUrl + '\'' +
               ", laborFunction='" + laborFunction + '\'' +
               ", location='" + location + '\'' +
               ", tags=" + tags +
               ", postedDatetime=" + postedDatetime +
               ", url='" + url + '\'' +
               '}';
    }
}
