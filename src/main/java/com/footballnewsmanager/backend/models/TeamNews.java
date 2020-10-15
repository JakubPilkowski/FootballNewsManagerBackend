package com.footballnewsmanager.backend.models;

import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Entity
public class TeamNews {

    @Id
    @GeneratedValue()
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    @ManyToOne
    private Team team;

    @ManyToOne
    private News news;

}
