package com.footballnewsmanager.backend.api.request.teams;

import com.footballnewsmanager.backend.models.TeamNews;

import java.util.List;

public class TeamsFromTagsRequest {

    private List<TeamNews> teamNews;

    public List<TeamNews> getTeamNews() {
        return teamNews;
    }
}
