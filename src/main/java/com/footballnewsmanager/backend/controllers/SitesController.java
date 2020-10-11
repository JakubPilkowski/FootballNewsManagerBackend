package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteWithClicks;
import com.footballnewsmanager.backend.api.response.sites.SitesResponse;
import com.footballnewsmanager.backend.exceptions.ValidationExceptionHandlers;
import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.SiteClick;
import com.footballnewsmanager.backend.repositories.SiteClickRepository;
import com.footballnewsmanager.backend.repositories.SiteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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


    public SitesController(SiteRepository siteRepository, SiteClickRepository siteClickRepository) {
        this.siteRepository = siteRepository;
        this.siteClickRepository = siteClickRepository;
    }


    @GetMapping("")
    public ResponseEntity<SitesResponse> getSites() {
        List<Site> sites = siteRepository.findAll();
        Set<SiteWithClicks> siteWithClicksSet = new HashSet<>();
        for (Site site :
                sites) {
            LocalDate localDate = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int skipNumber = localDate.get(weekFields.dayOfWeek());
            List<SiteClick> clicksFromLastWeek = siteClickRepository.findBySiteAndDateBetween(site, localDate.minusDays(6 + skipNumber), localDate.minusDays(skipNumber));
            SiteWithClicks siteWithClicks = new SiteWithClicks(site.getId(), site.getName(), site.getLogoUrl(),
                    site.getDescription(), site.isHighlighted(), 0);
            if (clicksFromLastWeek.size() > 0) {
                for (SiteClick siteClick :
                        clicksFromLastWeek) {
                    siteWithClicks.setClicks(siteWithClicks.getClicks() + siteClick.getClicks());
                }
            }
            siteWithClicksSet.add(siteWithClicks);
        }
        SitesResponse sitesResponse = new SitesResponse(true, "Strony", siteWithClicksSet);
        return ResponseEntity.ok(sitesResponse);
    }

    //role admin
    @PutMapping("/highlight/{id}")
    public ResponseEntity<BaseResponse> toggleHighlight(@PathVariable("id")
                                                        @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
                                                                Long id) {
        AtomicReference<SiteResponse> siteResponse = new AtomicReference<>();
        checkExistByIdAndOnSuccess(id, new Site(), siteRepository, "Nie ma takiej strony",
                (site) -> {
                    boolean highlighted = site.isHighlighted();
                    site.setHighlighted(!highlighted);
                    siteRepository.save(site);
                    siteResponse.set(new SiteResponse(true, "Wyróżniono stronę", site));
                });
        return ResponseEntity.ok(siteResponse.get());
    }

    @PutMapping("/click/{id}")
    public ResponseEntity<BaseResponse> addClickToSite(@Valid @PathVariable("id")
                                                       @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
                                                               Long id) {
        AtomicReference<BaseResponse> baseResponse = new AtomicReference<>();
        checkExistByIdAndOnSuccess(id, new Site(), siteRepository, "Nie ma takiej strony",
                (site) -> {
                    SiteClick click = siteClickRepository.findBySiteAndDate(site, LocalDate.now()).map((siteClick) -> {
                        int clicks = siteClick.getClicks();
                        siteClick.setClicks(clicks + 1);
                        return siteClick;
                    }).orElseGet(() -> {
                        SiteClick createdSiteClick = new SiteClick();
                        createdSiteClick.setSite(site);
                        createdSiteClick.setDate(LocalDate.now());
                        createdSiteClick.setClicks(1);
                        return createdSiteClick;
                    });
                    siteClickRepository.save(click);
                    baseResponse.set(new BaseResponse(true, "Dodano kliknięcie"));
                });
        return ResponseEntity.ok(baseResponse.get());
    }

    private <T, R extends JpaRepository<T, Long>> void checkExistByIdAndOnSuccess(Long id, T type, R repository, String message, OnPresentInterface<T> onPresentInterface) {
        repository.findById(id).map(result -> {
            onPresentInterface.onSuccess(result);
            return result;
        }).orElseThrow(() -> new ResourceNotFoundException(message));
    }
}
