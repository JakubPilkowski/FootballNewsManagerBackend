package com.footballnewsmanager.backend.models;

import java.io.Serializable;
import java.util.Objects;

public class NewsId implements Serializable {

    private int newsSiteId;
    private int newsId;

    public NewsId(){

    }

    public NewsId(int newsSiteId, int newsId) {
        this.newsSiteId = newsSiteId;
        this.newsId = newsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsId newsId = (NewsId) o;
        return newsSiteId == newsId.newsSiteId &&
                this.newsId == newsId.newsId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsSiteId, newsId);
    }
}
