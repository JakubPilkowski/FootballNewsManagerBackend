package com.footballnewsmanager.backend.models;


import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "site_clicks")
public class SiteClick {

    @Id
    @GeneratedValue()
    private Long id;

    @NotBlank(message = ValidationMessage.CLICKS_NOT_BLANK)
    private int clicks = 0;

    @NotBlank(message = ValidationMessage.DATE_NOT_BLANK)
    private LocalDate date;

    @ManyToOne()
    private Site site;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getClicks() {
        return clicks;
    }

    public LocalDate getDate() {
        return date;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
