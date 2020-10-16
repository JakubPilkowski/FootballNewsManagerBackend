package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.TeamsResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteWithClicks;
import com.footballnewsmanager.backend.api.response.sites.SitesResponse;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.SiteClick;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.repositories.SiteClickRepository;
import com.footballnewsmanager.backend.repositories.SiteRepository;
import com.footballnewsmanager.backend.services.BaseService;
import com.footballnewsmanager.backend.services.PaginationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/sites")
@Validated
public class SitesController {

    private final SiteRepository siteRepository;
    private final SiteClickRepository siteClickRepository;
    private final BaseService baseService;

    public SitesController(SiteRepository siteRepository, SiteClickRepository siteClickRepository, BaseService baseService) {
        this.siteRepository = siteRepository;
        this.siteClickRepository = siteClickRepository;
        this.baseService = baseService;
    }

    @GetMapping(value = "", params = {"page"})
    public ResponseEntity<SitesResponse> getSites(@RequestParam("page") @Min(value = 0) int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "highlighted", "popularity"));
        Page<Site> sites = siteRepository.findAll(pageable);
        PaginationService.handlePaginationErrors(page, sites);
        return ResponseEntity.ok(new SitesResponse(true, "Strony", sites.getContent()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/highlight/{id}")
    public ResponseEntity<BaseResponse> toggleHighlight(@PathVariable("id")
                                                        @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
                                                                Long id) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony"));
        boolean highlighted = site.isHighlighted();
        site.setHighlighted(!highlighted);
        siteRepository.save(site);
        return ResponseEntity.ok(new SiteResponse(true, "Wyróżniono stronę", site));
    }

    @PutMapping("/click/{id}")
    public ResponseEntity<BaseResponse> addClickToSite(@PathVariable("id")
                                                       @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
                                                               Long id) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nie ma takiej strony"));
        site.setClicks(site.getClicks() + 1);
        site.measurePopularity();
        siteRepository.save(site);
        return ResponseEntity.ok(new BaseResponse(true, "Dodano kliknięcie"));
    }

    @GetMapping(value = "query={query}", params = {"page"})
    public ResponseEntity<SitesResponse> getSitesByQuery(@RequestParam(value = "page", defaultValue = "0") @Min(value = 0) int page,
                                                         @PathVariable("query") @NotNull() String query) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "highlighted", "popularity"));
        Page<Site> sites = siteRepository.findByNameContainsIgnoreCase(query, pageable).orElseThrow(() -> new ResourceNotFoundException("Dla podanego hasła nie ma żadnej drużyny"));
        PaginationService.handlePaginationErrors(page, sites);
        return ResponseEntity.ok(new SitesResponse(true, "Znalezione strony", sites.getContent()));
    }

}
