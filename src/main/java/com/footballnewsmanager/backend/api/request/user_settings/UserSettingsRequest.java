package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.Team;

import java.util.List;

public class UserSettingsRequest {


    private List<Team> favouriteTeams;

//    private List<Site> chosenSites;

    public List<Team> getFavouriteTeams() {
        return favouriteTeams;
    }

    public void setFavouriteTeams(List<Team> favouriteTeams) {
        this.favouriteTeams = favouriteTeams;
    }

//    public List<Site> getChosenSites() {
//        return chosenSites;
//    }

//    public void setChosenSites(List<Site> chosenSites) {
//        this.chosenSites = chosenSites;
//    }
}
