package com.footballnewsmanager.backend.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Entity
public class SingleNewsTags {

    @Id
    @GeneratedValue()
    private int id;

    @NotBlank
    private String name;

    @ManyToOne
    private SingleNews singleNews;

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

    public SingleNews getNews() {
        return singleNews;
    }

    public void setNews(SingleNews singleNews) {
        this.singleNews = singleNews;
    }

}
