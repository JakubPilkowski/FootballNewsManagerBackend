package com.footballnewsmanager.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "markers",uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "name"
        })
})
@JsonView(Views.Public.class)
public class Marker {
    @Id
    @GeneratedValue()
    private Long id;

    @NotBlank
    private String name;

    @ManyToMany(mappedBy = "markers", fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<Team> teams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
