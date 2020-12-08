package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.models.Team;

import java.util.List;

public class TeamsRequest {

    private List<Team> teams;

    public List<Team> getTeams() {
        return teams;
    }
}
