package com.footballnewsmanager.backend.api.response.news;

import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.News;

public class SingleNewsResponse extends BaseResponse {

    private News news;

    public SingleNewsResponse(boolean success, String message, News news) {
        super(success, message);
        this.news = news;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }
}
