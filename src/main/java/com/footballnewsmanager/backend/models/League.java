package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonView(Views.Public.class)
@Table(name = "leagues")
public class League {

    @Id
    @GeneratedValue()
    private Long id;


    @NotBlank
    @JsonIgnore
    private int apisportid;

    @NotBlank
    @Size(min = 5)
    @Size(max = 50)
    private String name;

    @NotBlank
    private String logoUrl;


    @Enumerated(EnumType.STRING)
    @NotBlank
    private LeagueType type;

    @OneToMany(mappedBy = "league")
    @JsonBackReference
    private List<Team> teams = new ArrayList<>();

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

    @Enumerated(EnumType.STRING)
    public LeagueType getType() {
        return type;
    }

    @Enumerated(EnumType.STRING)
    public void setType(LeagueType type) {
        this.type = type;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public int getApisportid() {
        return apisportid;
    }

    public void setApisportid(int apisportid) {
        this.apisportid = apisportid;
    }
}
