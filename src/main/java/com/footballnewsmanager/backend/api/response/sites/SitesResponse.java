package com.footballnewsmanager.backend.api.response.sites;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class SitesResponse {

    private Long pages;
    private List<Site> sites;

    public SitesResponse(List<Site> sites, Long pages) {
        this.sites = sites;
        this.pages = pages;
    }

    public Long getPages() {
        return pages;
    }

    public List<Site> getSites() {
        return sites;
    }
}
