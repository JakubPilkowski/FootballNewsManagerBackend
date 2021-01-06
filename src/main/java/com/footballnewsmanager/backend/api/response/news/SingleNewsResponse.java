package com.footballnewsmanager.backend.api.response.news;

import com.footballnewsmanager.backend.models.UserNews;

public class SingleNewsResponse {

    private UserNews news;
    private SingleNewsType type;

    public UserNews getNews() {
        return news;
    }

    public void setNews(UserNews news) {
        this.news = news;
    }

    public SingleNewsType getType() {
        return type;
    }

    public void setType(SingleNewsType type) {
        this.type = type;
    }
}
