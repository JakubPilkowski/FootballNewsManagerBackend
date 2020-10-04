package com.footballnewsmanager.backend.api.response;

import com.footballnewsmanager.backend.models.News;

import java.util.List;

public class NewsResponse extends BaseResponse{

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
