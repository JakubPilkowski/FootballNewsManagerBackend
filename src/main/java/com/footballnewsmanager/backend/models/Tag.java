package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

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
    private Long id;

    @NotBlank(message = ValidationMessage.TAG_NAME_NOT_BLANK)
    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonBackReference
    private Set<NewsTag> tags = new HashSet<>();

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

    public Set<NewsTag> getTags() {
        return tags;
    }

    public void setTags(Set<NewsTag> tags) {
        this.tags = tags;
    }
}
