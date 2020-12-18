package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.constraints.Min;


@Entity
@JsonView(Views.Public.class)
public class UserNews {


    @Id
    @GeneratedValue()
    @JsonIgnore()
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long id;

    @ManyToOne
    private News news;

    @ManyToOne
    @JsonIgnore()
    private User user;

    private boolean liked =false;
    private boolean inFavourites =false;
    private boolean visited =false;
    private boolean badged =false;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isBadged() {
        return badged;
    }

    public void setBadged(boolean badged) {
        this.badged = badged;
    }

    public boolean isInFavourites() {
        return inFavourites;
    }

    public void setInFavourites(boolean inFavourites) {
        this.inFavourites = inFavourites;
    }
}
