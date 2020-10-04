package com.footballnewsmanager.backend.api.response;

import com.footballnewsmanager.backend.models.Team;

import java.util.List;

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
