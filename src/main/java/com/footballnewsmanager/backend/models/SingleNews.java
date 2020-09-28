package com.footballnewsmanager.backend.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SingleNews {

    @Id
    @GeneratedValue()
    private int id;

    @NotBlank
    @Size(min=4, max=100)
    private String title;

    @NotBlank
    private String newsUrl;

    @NotBlank
    private LocalDate date;

    @ManyToOne
    private Site site;

    @OneToMany(mappedBy = "singleNews")
    private List<SingleNewsTags> tags = new ArrayList<>();

    private int clicks =0;

    private boolean highlighted= false;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public List<SingleNewsTags> getTags() {
        return tags;
    }

    public void setTags(List<SingleNewsTags> tags) {
        this.tags = tags;
    }
}
