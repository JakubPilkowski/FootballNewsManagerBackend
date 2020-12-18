package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class NewsResponse {


    private List<UserNews> userNews;
    private Long newsCount;
    private Long newsToday;
    private int pages;

    public List<UserNews> getUserNews() {
        return userNews;
    }

    public void setUserNews(List<UserNews> userNews) {
        this.userNews = userNews;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public Long getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(Long newsCount) {
        this.newsCount = newsCount;
    }

    public Long getNewsToday() {
        return newsToday;
    }

    public void setNewsToday(Long newsToday) {
        this.newsToday = newsToday;
    }
}
