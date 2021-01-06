package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.models.UserTeam;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class AllNewsResponse extends NewsResponse{

    private List<UserTeam> proposedTeams;

    public List<UserTeam> getProposedTeams() {
        return proposedTeams;
    }

    public void setProposedTeams(List<UserTeam> proposedTeams) {
        this.proposedTeams = proposedTeams;
    }
}
