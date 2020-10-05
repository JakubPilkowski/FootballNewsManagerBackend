package com.footballnewsmanager.backend.controllers;


import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteResponse;
import com.footballnewsmanager.backend.api.response.sites.SiteWithClicks;
import com.footballnewsmanager.backend.api.response.sites.SitesResponse;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.SiteClick;
import com.footballnewsmanager.backend.repositories.SiteClickRepository;
import com.footballnewsmanager.backend.repositories.SiteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/sites")
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
            Optional<SiteClick> clicks = siteClickRepository.findBySiteAndDate(site, LocalDate.now());
            Optional<Integer> clicksFromLastWeek = siteClickRepository.sumFromLastWeek(site, LocalDate.now().minusDays(7), LocalDate.now());
            clicksFromLastWeek.ifPresent(System.out::println);

            if (clicks.isPresent()) {
                SiteWithClicks siteWithClicks = new SiteWithClicks(site.getId(), site.getName(), site.getLogoUrl(),
                        site.isHighlighted(), clicks.get().getClicks());
                siteWithClicksSet.add(siteWithClicks);
            }
            else{
                SiteWithClicks siteWithClicks = new SiteWithClicks(site.getId(), site.getName(), site.getLogoUrl(),
                        site.isHighlighted(), 0);
                siteWithClicksSet.add(siteWithClicks);
            }
        }

        SitesResponse sitesResponse = new SitesResponse(true, "Strony", siteWithClicksSet);
        return ResponseEntity.ok(sitesResponse);
    }

    //role admin
    @PutMapping("/highlight/{id}")
    public ResponseEntity<BaseResponse> toggleHighlight(@PathVariable("id") int id) {
        Optional<Site> site = siteRepository.findById(id);
        if (site.isPresent()) {
            boolean highlighted = site.get().isHighlighted();
            site.get().setHighlighted(!highlighted);
            siteRepository.save(site.get());
            return ResponseEntity.ok(new SiteResponse(true, "Wyróżniono stronę", site.get()));
        } else {
            return ResponseEntity.ok(new BaseResponse(false, "Nie ma takiej strony!!!"));
        }
    }

    @PutMapping("/click/{id}")
    public ResponseEntity<BaseResponse> addClickToSite(@PathVariable("id") int id) {
        Optional<Site> site = siteRepository.findById(id);
        if (site.isPresent()) {
            Optional<SiteClick> siteClick = siteClickRepository.findBySiteAndDate(site.get(), LocalDate.now());
            if (siteClick.isPresent()) {
                int clicks = siteClick.get().getClicks();
                siteClick.get().setClicks(clicks + 1);
                siteClickRepository.save(siteClick.get());
//                List<SiteClick> siteClicks = siteClickRepository.findBySite(site.get());
            } else {
                SiteClick createdSiteClick = new SiteClick();
                createdSiteClick.setSite(site.get());
                createdSiteClick.setDate(LocalDate.now());
                createdSiteClick.setClicks(1);
                siteClickRepository.save(createdSiteClick);
            }
//            int clicks = site.get().getClicks();
//            site.get().setClicks(clicks+1);
//            site.get().setClicks();
//            siteRepository.save(site.get());
            return ResponseEntity.ok(new SiteResponse(true, "Wyróżniono stronę", site.get()));
        } else {
            return ResponseEntity.ok(new BaseResponse(false, "Nie ma takiej strony!!!"));
        }
    }


}
