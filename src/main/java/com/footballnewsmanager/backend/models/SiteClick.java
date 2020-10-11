package com.footballnewsmanager.backend.models;


import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "site_clicks")
public class SiteClick {

    @Id
    @GeneratedValue()
    private Long id;

    @NotNull(message = ValidationMessage.CLICKS_NOT_BLANK)
    @Min(value = 0, message = ValidationMessage.CLICKS_LESS_THAN_ZERO)
    private int clicks = 0;

    @NotNull(message = ValidationMessage.DATE_NOT_BLANK)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
