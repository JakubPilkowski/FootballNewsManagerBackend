package com.footballnewsmanager.backend.api.response.news;

public class BaseNewsAdjustment {

    private String title;
    private NewsInfoType type;

    public BaseNewsAdjustment(String title, NewsInfoType type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NewsInfoType getType() {
        return type;
    }

    public void setType(NewsInfoType type) {
        this.type = type;
    }
}
