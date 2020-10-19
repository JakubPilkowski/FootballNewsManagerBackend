package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class TeamExtras extends BaseNewsAdjustment{

    private List<Team> teams;

    public TeamExtras(String title, NewsInfoType type, List<Team> teams) {
        super(title, type);
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
