package com.footballnewsmanager.backend.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "team")
    private List<TeamTags> tags = new ArrayList<>();


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


    public List<TeamTags> getTags() {
        return tags;
    }

    public void setTags(List<TeamTags> tags) {
        this.tags = tags;
    }
}
