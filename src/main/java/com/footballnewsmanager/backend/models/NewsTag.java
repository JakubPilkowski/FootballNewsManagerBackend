package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "news_tags")
public class NewsTag {

    @Id
    @GeneratedValue()
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
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
