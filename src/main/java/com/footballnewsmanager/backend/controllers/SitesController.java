package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteWithClicks;
import com.footballnewsmanager.backend.api.response.sites.SitesResponse;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.SiteClick;
import com.footballnewsmanager.backend.repositories.SiteClickRepository;
import com.footballnewsmanager.backend.repositories.SiteRepository;
import com.footballnewsmanager.backend.services.BaseService;
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
    private final BaseService baseService;

    public SitesController(SiteRepository siteRepository, SiteClickRepository siteClickRepository, BaseService baseService) {
        this.siteRepository = siteRepository;
        this.siteClickRepository = siteClickRepository;
        this.baseService = baseService;
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
        Site site = baseService.checkExistByIdAndOnSuccess(id, new Site(), siteRepository, "Nie ma takiej strony",
                (siteFromDB) -> {
                    boolean highlighted = siteFromDB.isHighlighted();
                    siteFromDB.setHighlighted(!highlighted);
                    siteRepository.save(siteFromDB);
                    return siteFromDB;
                });
        return ResponseEntity.ok(new SiteResponse(true, "Wyróżniono stronę", site));
    }

    @PutMapping("/click/{id}")
    public ResponseEntity<BaseResponse> addClickToSite(@PathVariable("id")
                                                       @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
                                                               Long id) {
        baseService.checkExistByIdAndOnSuccess(id, new Site(), siteRepository, "Nie ma takiej strony",
                (siteFromDB) -> {
                    SiteClick click = siteClickRepository.findBySiteAndDate(siteFromDB, LocalDate.now()).map((siteClick) -> {
                        int clicks = siteClick.getClicks();
                        siteClick.setClicks(clicks + 1);
                        return siteClick;
                    }).orElseGet(() -> {
                        SiteClick createdSiteClick = new SiteClick();
                        createdSiteClick.setSite(siteFromDB);
                        createdSiteClick.setDate(LocalDate.now());
                        createdSiteClick.setClicks(1);
                        return createdSiteClick;
                    });
                    siteClickRepository.save(click);
                    return siteFromDB;
                });
        return ResponseEntity.ok(new BaseResponse(true, "Dodano kliknięcie"));
    }
}
