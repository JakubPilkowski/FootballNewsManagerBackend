package com.footballnewsmanager.backend.api.response.sites;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class SitesResponse extends BaseResponse {

    private List<Site> sites;

    public SitesResponse(boolean success, String message, List<Site> sites) {
        super(success, message);
        this.sites = sites;
    }

    public List<Site> getSites() {
        return sites;
    }
}
