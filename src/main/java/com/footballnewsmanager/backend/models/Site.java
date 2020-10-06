package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sites")
public class Site {

    @Id
    @GeneratedValue()
    private int id;

    @NotBlank
    @Size(min=4, max = 100)
    private String name;

    @NotBlank
    private String logoUrl;

    @NotBlank
    @Size(min=10, max=150)
    private String description;


    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<News> news = new ArrayList<>();

    @OneToMany(mappedBy = "site")
    @JsonBackReference
    private List<SiteClick> clicks = new ArrayList<>();


    //clicks = siteClicksRepo.findbySiteAndDateBetween

    private boolean highlighted = false;


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





    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> aNews) {
        this.news = aNews;
    }

    public List<SiteClick> getClicks() {
        return clicks;
    }

    public void setClicks(List<SiteClick> clicks) {
        this.clicks = clicks;
    }
}
