package com.footballnewsmanager.backend.api.request.news;

import com.footballnewsmanager.backend.models.Team;

import java.util.List;

public class NewsForTeamsRequest {
    private List<Team> teams;

    public List<Team> getTeams() {
        return teams;
    }
}
