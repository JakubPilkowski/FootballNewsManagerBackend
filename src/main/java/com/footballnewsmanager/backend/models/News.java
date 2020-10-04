package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private int newsSiteId;

    @Id
    private int newsId;


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
    private Site site;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<NewsTag> tags = new HashSet<>();

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "news_tags",
//            joinColumns =  { @JoinColumn( name = "news_site_id"), @JoinColumn(name="news_id") },
//            inverseJoinColumns = @JoinColumn(name = "tags_id"))
//    private Set<Tag> tags;


    public List<TeamNews> getTeamNews() {
        return teamNews;
    }

    public void setTeamNews(List<TeamNews> teamNews) {
        this.teamNews = teamNews;
    }

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL,orphanRemoval = true)
//    @JsonManagedReference
    @JsonIgnore()
    private List<TeamNews> teamNews = new ArrayList<>();


    private int clicks =0;

    private boolean highlighted= false;



//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

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

    public int getNewsSiteId() {
        return newsSiteId;
    }

    public void setNewsSiteId(int site_id) {
        this.newsSiteId = site_id;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int news_id) {
        this.newsId = news_id;
    }

    public Set<NewsTag> getTags() {
        return tags;
    }

    public void setTags(Set<NewsTag> tags) {
        this.tags = tags;
    }
}
