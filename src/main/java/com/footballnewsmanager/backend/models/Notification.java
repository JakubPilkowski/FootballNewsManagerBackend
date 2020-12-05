package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.views.Views;

@JsonView(Views.Public.class)
public class Notification {

    private Team team;
    private Long amountBefore;

    private Long amountAfter;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Long getAmountAfter() {
        return amountAfter;
    }

    public void setAmountAfter(Long amountAfter) {
        this.amountAfter = amountAfter;
    }

    public Long getAmountBefore() {
        return amountBefore;
    }

    public void setAmountBefore(Long amountBefore) {
        this.amountBefore = amountBefore;
    }
}
