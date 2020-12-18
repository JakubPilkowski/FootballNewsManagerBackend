package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.views.Views;

@JsonView(Views.Public.class)
public class SearchResult {

    private String name;
    private Long id;
    private SearchType type;
    private String newsUrl;
    private String imgUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
}
