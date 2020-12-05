package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.helpers.Multipliers;
import com.footballnewsmanager.backend.views.Views;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@IdClass(NewsId.class)
@JsonView(Views.Public.class)
public class News {

    @Id
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long siteId;

    @Id
    @NotNull(message = ValidationMessage.ID_NOT_NULL)
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long id;


    @NotBlank(message = ValidationMessage.NEWS_TITLE_NOT_BLANK)
    @Size(min = 4, max = 250, message = ValidationMessage.NEWS_TITLE_SIZE)
    private String title;

    @NotBlank(message = ValidationMessage.NEWS_URL_NOT_BLANK)
    private String newsUrl;

    @NotBlank(message = ValidationMessage.IMAGE_NOT_BLANK)
    private String imageUrl;

    @NotNull(message = ValidationMessage.DATE_NOT_BLANK)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "site")
    @JsonView(Views.Public.class)
    private Site site;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.Public.class)
    private Set<NewsTag> tags = new HashSet<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore()
    private List<TeamNews> teamNews = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserNewsLike> userLikes = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserNewsDislike> userDislikes = new ArrayList<>();

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @JsonIgnore
    private double popularity = 0L;

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @Min(value = 0, message = ValidationMessage.CLICKS_LESS_THAN_ZERO)
    private Long clicks = 0L;

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @Min(value = 0, message = ValidationMessage.CLICKS_LESS_THAN_ZERO)
    private Long likes = 0L;

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @Min(value = 0, message = ValidationMessage.CLICKS_LESS_THAN_ZERO)
    private Long dislikes = 0L;

    private boolean highlighted = false;

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

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
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

    public List<TeamNews> getTeamNews() {
        return teamNews;
    }

    public void setTeamNews(List<TeamNews> teamNews) {
        this.teamNews = teamNews;
    }

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

    public List<UserNewsLike> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(List<UserNewsLike> userLikes) {
        this.userLikes = userLikes;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public void measurePopularity(){
        setPopularity(getClicks()* Multipliers.CLICK_MULTIPLIER
                +getLikes()* Multipliers.LIKED - getDislikes()*Multipliers.LIKED);
    }

    public List<UserNewsDislike> getUserDislikes() {
        return userDislikes;
    }

    public void setUserDislikes(List<UserNewsDislike> userDislikes) {
        this.userDislikes = userDislikes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }
}
