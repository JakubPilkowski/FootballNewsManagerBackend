package com.footballnewsmanager.backend.api.response.news;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.UserNews;
import com.footballnewsmanager.backend.views.Views;

import java.util.List;

@JsonView(Views.Public.class)
public class NewsResponse {


    private List<UserNews> news;

    public List<UserNews> getNews() {
        return news;
    }

    public void setNews(List<UserNews> news) {
        this.news = news;
    }
}
