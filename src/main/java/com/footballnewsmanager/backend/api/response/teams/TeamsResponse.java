package com.footballnewsmanager.backend.api.response.teams;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.UserTeam;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class TeamsResponse {

    private int pages;
    private List<UserTeam> teams;

    public void setTeams(List<UserTeam> teams) {
        this.teams = teams;
    }

    public List<UserTeam> getTeams() {
        return teams;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
