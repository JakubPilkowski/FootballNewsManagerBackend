package com.footballnewsmanager.backend.models;


import javax.persistence.*;

@Entity
@Table(name = "favourite_teams")
public class FavouriteTeam {

    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne
    private Team team;

    @ManyToOne
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
