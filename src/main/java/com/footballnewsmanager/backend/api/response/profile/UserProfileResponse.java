package com.footballnewsmanager.backend.api.response.profile;

import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.models.User;
import com.footballnewsmanager.backend.views.Views;

@JsonView(Views.Public.class)
public class UserProfileResponse {

    private Long likes;
    private Long favouritesCount;
    private User user;

    public UserProfileResponse(User user, Long likes, Long favouritesCount) {
        this.user = user;
        this.likes = likes;
        this.favouritesCount = favouritesCount;
    }

    public User getUser() {
        return user;
    }

    public Long getLikes() {
        return likes;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(Long favouritesCount) {
        this.favouritesCount = favouritesCount;
    }
}
