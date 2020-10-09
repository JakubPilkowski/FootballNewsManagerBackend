package com.footballnewsmanager.backend.api.request.user_settings;

import com.footballnewsmanager.backend.models.Language;
import com.footballnewsmanager.backend.models.Site;
import com.footballnewsmanager.backend.models.Team;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class UserSettingsRequest {

    @NotBlank
    private List<Team> favouriteTeams;

    @NotBlank
    private List<Site> chosenSites;

//    @Enumerated(EnumType.STRING)
    @NotBlank
    private Language language;

    @NotBlank
    boolean notifications;

    @NotBlank
    boolean darkMode;

    @NotBlank
    boolean proposedNews;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public boolean isProposedNews() {
        return proposedNews;
    }

    public void setProposedNews(boolean proposedNews) {
        this.proposedNews = proposedNews;
    }

    public @NotBlank List<Team> getFavouriteTeams() {
        return favouriteTeams;
    }

    public void setFavouriteTeams(@NotBlank List<Team> favouriteTeams) {
        this.favouriteTeams = favouriteTeams;
    }

    public List<Site> getChosenSites() {
        return chosenSites;
    }

    public void setChosenSites(List<Site> chosenSites) {
        this.chosenSites = chosenSites;
    }
}
