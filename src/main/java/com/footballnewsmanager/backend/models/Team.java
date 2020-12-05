package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.helpers.Multipliers;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams")
@JsonView(Views.Public.class)
public class Team {


    @Id
    @GeneratedValue()
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long id;

    @NotBlank(message = ValidationMessage.TEAM_NAME_NOT_BLANK)
    @Size(min = 3, max = 50, message = ValidationMessage.TEAM_NAME_SIZE)
    private String name;

    @NotBlank(message = ValidationMessage.LOGO_NOT_BLANK)
    private String logoUrl;


    @Min(value = 0, message = ValidationMessage.POPULARITY_LESS_THAN_ZERO)
    @JsonView(Views.Internal.class)
    private double popularity = 0f;

    @Min(value = 0, message = ValidationMessage.POPULARITY_LESS_THAN_ZERO)
    @JsonView(Views.Internal.class)
    private Long clicks = 0L;

    @Min(value = 0, message = ValidationMessage.POPULARITY_LESS_THAN_ZERO)
    @JsonView(Views.Internal.class)
    private Long newsCount = 0L;

    @Min(value = 0, message = ValidationMessage.POPULARITY_LESS_THAN_ZERO)
    @JsonView(Views.Internal.class)
    private Long chosenAmount = 0L;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.Internal.class)
    private Set<Marker> markers;

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<TeamNews> teamNews = new ArrayList<>();

    @ManyToOne
    @JsonView(Views.Internal.class)
    private League league;

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<FavouriteTeam> userTeams = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Set<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(Set<Marker> markers) {
        this.markers = markers;
    }

    public List<TeamNews> getTeamNews() {
        return teamNews;
    }

    public void setTeamNews(List<TeamNews> teamNews) {
        this.teamNews = teamNews;
    }

    public List<FavouriteTeam> getUserTeams() {
        return userTeams;
    }

    public void setUserTeams(List<FavouriteTeam> userTeams) {
        this.userTeams = userTeams;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public Long getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(Long newsCount) {
        this.newsCount = newsCount;
    }

    public void measurePopularity() {
        setPopularity(getClicks() * Multipliers.CLICK_MULTIPLIER
                + getNewsCount() * Multipliers.NEWS_MULTIPLIER
                + getChosenAmount() * Multipliers.CHOSEN_BY_MULTIPLIER);
    }

    public Long getChosenAmount() {
        return chosenAmount;
    }

    public void setChosenAmount(Long chosenAmount) {
        this.chosenAmount = chosenAmount;
    }
}
