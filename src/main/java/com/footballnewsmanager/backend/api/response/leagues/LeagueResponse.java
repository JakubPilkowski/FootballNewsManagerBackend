package com.footballnewsmanager.backend.api.response.leagues;

import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.League;

import java.util.List;

public class LeagueResponse extends BaseResponse {


    public LeagueResponse(boolean success, String message, List<League> leagues) {
        super(success, message);
        this.leagues = leagues;
    }

    private List<League> leagues;

    public List<League> getLeagues() {
        return leagues;
    }
}
