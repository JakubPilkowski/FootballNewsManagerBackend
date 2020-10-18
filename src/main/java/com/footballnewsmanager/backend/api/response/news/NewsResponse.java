package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.News;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class NewsResponse extends BaseResponse {


    private List<News> news;

    public NewsResponse(boolean success, String message, List<News> news) {
        super(success, message);
        this.news = news;
    }


    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}
