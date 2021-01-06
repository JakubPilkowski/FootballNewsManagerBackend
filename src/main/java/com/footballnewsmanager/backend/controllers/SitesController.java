package com.footballnewsmanager.backend.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.sites.SitesResponse;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.repositories.SiteRepository;
import com.footballnewsmanager.backend.services.PaginationService;
import com.footballnewsmanager.backend.views.Views;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/sites")
@Validated
public class SitesController {

    private final SiteRepository siteRepository;

    public SitesController(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @GetMapping(value = "", params = {"page"})
    @JsonView(Views.Public.class)
    public ResponseEntity<SitesResponse> getSites(@RequestParam("page") @Min(value = 0) int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Site> sites = siteRepository.findAll(pageable);
        PaginationService.handlePaginationErrors(page, sites);
        Long pages = sites.getTotalElements();
        return ResponseEntity.ok(new SitesResponse(sites.getContent(), pages));
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

}
