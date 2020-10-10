package com.footballnewsmanager.backend.models;

import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "black_list_tokens")
public class BlackListToken {

    @Id
    @GeneratedValue()
    private Long id;

    @NotBlank(message= ValidationMessage.TOKEN_NOT_BLANK)
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
