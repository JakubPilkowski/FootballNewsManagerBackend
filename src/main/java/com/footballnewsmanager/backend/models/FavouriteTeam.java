package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;

@Entity
@Table(name = "favourite_teams")
@JsonView(Views.Public.class)
public class FavouriteTeam {

    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    @JsonBackReference()
    private User user;


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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
