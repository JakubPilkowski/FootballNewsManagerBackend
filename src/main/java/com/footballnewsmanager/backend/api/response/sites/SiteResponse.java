package com.footballnewsmanager.backend.api.response.sites;

import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.Site;

public class SiteResponse extends BaseResponse {
    private Site site;

    public SiteResponse(boolean success, String message, Site site) {
        super(success, message);
        this.site = site;
    }

    public Site getSite() {
        return site;
    }
}
