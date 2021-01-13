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
import java.time.LocalDateTime;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "site")
    private Site site;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamNews> teamNews = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore()
    private List<UserNews> userNews = new ArrayList<>();

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @JsonIgnore
    private double popularity = 0L;

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @Min(value = 0, message = ValidationMessage.CLICKS_LESS_THAN_ZERO)
    private Long clicks = 0L;

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @Min(value = 0, message = ValidationMessage.CLICKS_LESS_THAN_ZERO)
    private Long likes = 0L;


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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(@NotNull LocalDateTime date) {
        this.date = date;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
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

    public void measurePopularity() {
        setPopularity(getClicks() * Multipliers.CLICK_MULTIPLIER
                + getLikes() * Multipliers.LIKED);
    }

    public List<UserNews> getUserNews() {
        return userNews;
    }

    public void setUserNews(List<UserNews> userNews) {
        this.userNews = userNews;
    }
}
