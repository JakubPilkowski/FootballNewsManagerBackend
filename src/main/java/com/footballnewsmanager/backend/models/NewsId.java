package com.footballnewsmanager.backend.models;

import java.io.Serializable;
import java.util.Objects;

public class NewsId implements Serializable {

    private Long siteId;
    private Long id;

    public NewsId(){

    }

    public NewsId(Long siteId, Long id) {
        this.siteId = siteId;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsId newsId = (NewsId) o;
        return siteId.equals(newsId.siteId) &&
                this.id.equals(newsId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteId, id);
    }
}
