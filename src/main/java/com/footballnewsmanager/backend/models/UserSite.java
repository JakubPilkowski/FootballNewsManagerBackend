package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.views.Views;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_sites")
@JsonView(Views.Public.class)
public class UserSite {

    @Id
    @GeneratedValue()
    @NotNull(message = ValidationMessage.ID_NOT_NULL)
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    private Site site;

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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
