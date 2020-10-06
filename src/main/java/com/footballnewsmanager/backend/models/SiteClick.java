package com.footballnewsmanager.backend.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class SiteClick {

    @Id
    @GeneratedValue()
    private int id;

    private int clicks = 0;

    private LocalDate date;

    @ManyToOne()
    private Site site;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
