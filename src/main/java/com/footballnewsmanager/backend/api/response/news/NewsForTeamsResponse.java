package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class NewsForTeamsResponse<T extends BaseNewsAdjustment> {


    private List<News> news;

    private T additionalContent;

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public T getAdditionalContent() {
        return additionalContent;
    }

    public void setAdditionalContent(T additionalContent) {
        this.additionalContent = additionalContent;
    }
}
