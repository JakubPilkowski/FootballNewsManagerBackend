package com.footballnewsmanager.backend.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "name"
        })
})
public class TeamTags {
    @Id
    @GeneratedValue()
    private int id;

    @NotBlank
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Team> teams;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }
}
