package com.footballnewsmanager.backend.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
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


    @OneToMany(mappedBy = "site")
    private List<SingleNews> singleNews = new ArrayList<>();

    private int clicks = 0;

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



    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
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

    public List<SingleNews> getNews() {
        return singleNews;
    }

    public void setNews(List<SingleNews> singleNews) {
        this.singleNews = singleNews;
    }
}
