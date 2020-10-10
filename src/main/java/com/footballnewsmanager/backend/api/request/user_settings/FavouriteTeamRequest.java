package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.models.Team;

import javax.validation.Valid;

public class FavouriteTeamRequest {

    private Team team;

    public Team getTeam() {
        return team;
    }
}
