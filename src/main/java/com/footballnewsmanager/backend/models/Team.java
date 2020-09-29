package com.footballnewsmanager.backend.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
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
            name = "team_tag",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "team_tags_id"))
    private Set<TeamTags> tags;


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


    public Set<TeamTags> getTags() {
        return tags;
    }

    public void setTags(Set<TeamTags> tags) {
        this.tags = tags;
    }
}
