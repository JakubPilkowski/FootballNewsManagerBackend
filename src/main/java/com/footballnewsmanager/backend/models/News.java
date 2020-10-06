package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@IdClass(NewsId.class)
public class News {

    @Id
    private Long siteId;

    @Id
    private Long id;


    @NotBlank
    @Size(min=4, max=100)
    private String title;

    @NotBlank
    private String newsUrl;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "site")
    private Site site;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<NewsTag> tags = new HashSet<>();


    public List<TeamNews> getTeamNews() {
        return teamNews;
    }

    public void setTeamNews(List<TeamNews> teamNews) {
        this.teamNews = teamNews;
    }

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore()
    private List<TeamNews> teamNews = new ArrayList<>();


    private int clicks =0;

    private boolean highlighted= false;


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

//    public Set<Tag> getTags() {
//        return tags;
//    }
//
//    public void setTags(Set<Tag> tags) {
//        this.tags = tags;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long site_id) {
        this.siteId = site_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long news_id) {
        this.id = news_id;
    }

    public Set<NewsTag> getTags() {
        return tags;
    }

    public void setTags(Set<NewsTag> tags) {
        this.tags = tags;
    }
}
