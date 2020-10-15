package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sites")
@JsonView(Views.Public.class)
public class Site {

    @Id
    @GeneratedValue()
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long id;

    @NotBlank(message = ValidationMessage.SITE_NAME_NOT_BLANK)
    @Size(min = 4, max = 100, message = ValidationMessage.SITE_NAME_SIZE)
    private String name;

    @NotBlank(message = ValidationMessage.LOGO_NOT_BLANK)
    private String logoUrl;

    @NotBlank(message = ValidationMessage.SITE_DESCRIPTION_NOT_BLANK)
    @Size(min = 10, max = 150, message = ValidationMessage.SITE_DESCRIPTION_SIZE)
    private String description;

    @JsonView(Views.Internal.class)
    private boolean highlighted = false;

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY)
    @JsonBackReference(value = "news")
    private List<News> news = new ArrayList<>();

    @OneToMany(mappedBy = "site")
    @JsonBackReference(value = "clicks")
    private List<SiteClick> clicks = new ArrayList<>();

    @OneToMany(mappedBy = "site")
    @JsonBackReference(value = "userSites")
    private List<UserSite> userSites = new ArrayList<>();

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


    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public List<UserSite> getUserSites() {
        return userSites;
    }

    public void setUserSites(List<UserSite> userSites) {
        this.userSites = userSites;
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
