package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue()
    private int id;

    @NotBlank
    @Size(min=4, max = 50)
    private String name;

    //prawa autorskie do zdjęć, zobaczymy czy zostanie to pole
    @NotBlank
    private String logoUrl;

    @ManyToMany()
    @JoinTable(
            name = "team_markers",
            joinColumns = @JoinColumn(name = "teams_id"),
            inverseJoinColumns = @JoinColumn(name = "markers_id"))
    private Set<Marker> markers;


    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<TeamNews> teamNews= new ArrayList<>();

    @ManyToOne
    private League league;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
