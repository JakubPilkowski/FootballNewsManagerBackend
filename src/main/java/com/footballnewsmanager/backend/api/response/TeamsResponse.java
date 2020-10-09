package com.footballnewsmanager.backend.api.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.Team;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class TeamsResponse extends BaseResponse{

    private List<Team> teams;


    public TeamsResponse(boolean success, String message, List<Team> teams) {
        super(success, message);
        this.teams = teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }
}
