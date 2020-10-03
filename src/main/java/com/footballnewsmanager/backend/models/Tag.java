package com.footballnewsmanager.backend.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "name"
        })
})
public class Tag {

    @Id
    @GeneratedValue()
    private int id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<NewsTag> tags = new HashSet<>();

//    @ManyToMany(mappedBy = "tags")
//    private Set<News> news;

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

    public Set<NewsTag> getTags() {
        return tags;
    }

    public void setTags(Set<NewsTag> tags) {
        this.tags = tags;
    }

//    public Set<News> getNews() {
//        return news;
//    }
//
//    public void setNews(Set<News> news) {
//        this.news = news;
//    }
}
