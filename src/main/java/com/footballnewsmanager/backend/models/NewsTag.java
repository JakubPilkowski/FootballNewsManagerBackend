package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "news_tags")
public class NewsTag {

    @Id
    @GeneratedValue()
    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    @JsonBackReference
    private News news;

    @ManyToOne
    private Tag tag;

}
