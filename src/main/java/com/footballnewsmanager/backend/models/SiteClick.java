package com.footballnewsmanager.backend.models;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "site_clicks")
public class SiteClick {

    @Id
    @GeneratedValue()
    private Long id;

    private int clicks = 0;

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