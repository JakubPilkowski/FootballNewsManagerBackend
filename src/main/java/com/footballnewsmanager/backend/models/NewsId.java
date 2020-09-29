package com.footballnewsmanager.backend.models;

import java.io.Serializable;
import java.util.Objects;

public class NewsId implements Serializable {

    private int news_site_id;
    private int news_id;

    public NewsId(){

    }

    public NewsId(int news_site_id, int news_id) {
        this.news_site_id = news_site_id;
        this.news_id = news_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsId newsId = (NewsId) o;
        return news_site_id == newsId.news_site_id &&
                news_id == newsId.news_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(news_site_id, news_id);
    }
}
