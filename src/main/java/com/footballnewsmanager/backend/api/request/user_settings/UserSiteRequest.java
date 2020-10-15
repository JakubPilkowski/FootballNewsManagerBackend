package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.models.Site;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class UserSiteRequest {

    @Valid
    @NotNull(message = ValidationMessage.REQUEST_INVALID)
    private Site site;

    public Site getSite() {
        return site;
    }
}
