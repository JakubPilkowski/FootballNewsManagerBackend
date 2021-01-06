package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "user_teams")
@JsonView(Views.Public.class)
public class UserTeam {

    @Id
    @GeneratedValue()
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    @JsonIgnore()
    private Long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    @JsonBackReference()
    private User user;

    private boolean favourite = false;

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

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
