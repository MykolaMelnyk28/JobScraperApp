package com.mykolamelnyk.JobScraperApp.controller;

import com.mykolamelnyk.JobScraperApp.model.entity.Job;
import com.mykolamelnyk.JobScraperApp.model.dto.JobFilter;
import com.mykolamelnyk.JobScraperApp.model.entity.Tag;
import com.mykolamelnyk.JobScraperApp.service.JobService;
import com.mykolamelnyk.JobScraperApp.service.TagService;
import com.mykolamelnyk.JobScraperApp.util.scratching.TechstarsFilterBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TagService tabService;
    private final JobService jobService;

    private static final List<String> jobFunctions = List.of(
        "Software Engineering",
        "Sales & Business Development",
        "Operations",
        "Marketing & Communications",
        "IT",
        "Product",
        "Data Science",
        "Design",
        "Customer Service",
        "Quality Assurance",
        "Other Engineering",
        "Accounting & Finance",
        "People & HR",
        "Administration",
        "Legal",
        "Compliance / Regulatory"
    );

    @GetMapping
    public String home(
        @PageableDefault(page = 0, size = 20, sort = "positionName") Pageable pageable,
        @ModelAttribute JobFilter filter,
        Model model
    ) {
        Page<Job> page = jobService.getAll(filter, pageable);

        List<String> organizations = jobService.getAllOrganizationTitles();
        List<String> locations = jobService.getAllLocations();
        List<Tag> tags = tabService.getAll();

        model.addAttribute("items", page);
        model.addAttribute("filter", filter);
        model.addAttribute("organizations", organizations);
        model.addAttribute("laborFunctions", jobFunctions);
        model.addAttribute("locations", locations);
        model.addAttribute("tags", tags);
        model.addAttribute("jobFunctions", jobFunctions);
        if (!model.containsAttribute("syncTotalSize")) {
            model.addAttribute("syncTotalSize", jobService.getSynchronizeTotalSize());
        }
        return "index";
    }

    @PostMapping("/synchronize")
    public String synchronizePost(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(required = false) List<String> laborFunctions
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<String> functions = (laborFunctions == null) ? List.of() : laborFunctions;

        if (functions.contains("All")) {
            functions = List.of();
        }

        TechstarsFilterBuilder builder = new TechstarsFilterBuilder();
        builder.withJobFunctions(functions);

        Map<String, Object> filter = builder.build();

        jobService.synchronize(filter, pageable);

        return "redirect:/";
    }

}
