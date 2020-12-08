package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class NewsForTeamsResponse<T extends BaseNewsAdjustment> extends NewsResponse{

    private int pages;
    private Long newsCount;
    private Long newsToday;
    private T additionalContent;

    public T getAdditionalContent() {
        return additionalContent;
    }

    public void setAdditionalContent(T additionalContent) {
        this.additionalContent = additionalContent;
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
