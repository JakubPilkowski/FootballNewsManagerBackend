package com.footballnewsmanager.backend.api.response.news;

import com.footballnewsmanager.backend.models.News;

public class NewsExtras extends BaseNewsAdjustment {

    private News news;

    public NewsExtras(String title, NewsInfoType type, News news) {
        super(title, type);
        this.news = news;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }
}
