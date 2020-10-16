package com.footballnewsmanager.backend.api.response.sites;

import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.Site;

import java.util.List;

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
