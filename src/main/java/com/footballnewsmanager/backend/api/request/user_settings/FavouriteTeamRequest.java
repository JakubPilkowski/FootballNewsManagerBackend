package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.models.Team;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FavouriteTeamRequest {

    @Valid
    @NotNull(message = ValidationMessage.REQUEST_INVALID)
    private Team team;

    public Team getTeam() {
        return team;
    }
}
