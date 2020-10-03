package com.footballnewsmanager.backend.models;

import javax.persistence.*;

@Entity
@Table(name = "news_tags")
public class NewsTag {

    @Id
    @GeneratedValue()
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @ManyToOne
    private News news;

    @ManyToOne
    private Tag tag;

}
