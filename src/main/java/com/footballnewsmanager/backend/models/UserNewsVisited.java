package com.footballnewsmanager.backend.models;

import javax.persistence.*;


@Entity
@Table(name = "user_news_visited")
public class UserNewsVisited {
    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private News news;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }
}
