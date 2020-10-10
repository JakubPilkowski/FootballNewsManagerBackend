package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams")
//@JsonFilter("teamFilter")
@JsonView(Views.Public.class)
public class Team {

    @Id
    @GeneratedValue()
    private Long id;

    @NotBlank(message = ValidationMessage.TEAM_NAME_NOT_BLANK)
    @Size(min=3, max = 50, message = ValidationMessage.TEAM_NAME_SIZE)
    private String name;

    @NotBlank(message = ValidationMessage.LOGO_NOT_BLANK)
    private String logoUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_markers",
            joinColumns = @JoinColumn(name = "teams_id"),
            inverseJoinColumns = @JoinColumn(name = "markers_id"))
    @JsonView(Views.Internal.class)
    private Set<Marker> markers;

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<TeamNews> teamNews= new ArrayList<>();

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
}
