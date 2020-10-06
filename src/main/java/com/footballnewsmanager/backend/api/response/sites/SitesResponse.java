package com.footballnewsmanager.backend.api.response.sites;

import com.footballnewsmanager.backend.api.response.BaseResponse;

import java.util.Set;

public class SitesResponse extends BaseResponse {

    private Set<SiteWithClicks> sites;

    public SitesResponse(boolean success, String message, Set<SiteWithClicks> sites) {
        super(success, message);
        this.sites = sites;
    }

    public Set<SiteWithClicks> getSites() {
        return sites;
    }
}
