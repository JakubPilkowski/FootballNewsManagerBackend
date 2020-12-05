package com.footballnewsmanager.backend.api.response.news;

import com.footballnewsmanager.backend.api.response.BaseResponse;
import com.footballnewsmanager.backend.models.UserNews;

public class SingleNewsResponse extends BaseResponse {

    private UserNews news;

    public SingleNewsResponse(boolean success, String message, UserNews news) {
        super(success, message);
        this.news = news;
    }

    public UserNews getNews() {
        return news;
    }

    public void setNews(UserNews news) {
        this.news = news;
    }
}
