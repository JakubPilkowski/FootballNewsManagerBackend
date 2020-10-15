package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.validators.EnumNamePattern;
import com.footballnewsmanager.backend.views.Views;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })}
)
@JsonView(Views.Public.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 0, message = ValidationMessage.ID_LESS_THAN_ZERO)
    private Long id;


    @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
    @Size(min=4, max = 20, message = ValidationMessage.USERNAME_SIZE)
    private String username;

    @NaturalId(mutable = true)
    @NotBlank(message = ValidationMessage.EMAIL_NOT_BLANK)
    @Size(max = 40, message = ValidationMessage.EMAIL_SIZE)
    @Email(message = ValidationMessage.EMAIL_VALID)
    @JsonIgnore
    private String email;

    @NotBlank(message = ValidationMessage.PASSWORD_SIZE)
    @Size(max = 60)
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavouriteTeam> favouriteTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSite> userSites = new ArrayList<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JsonView(Views.Internal.class)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    private boolean darkMode = true;

    @Enumerated(EnumType.STRING)
    @NotNull(message = ValidationMessage.LANGUAGE_NOT_BLANK)
    @EnumNamePattern(regexp = "POLSKI|ANGIELSKI|WŁOSKI|FRANCUSKI|NIEMIECKI|HISZPAŃSKI", message = ValidationMessage.LANGUAGE_INVALID)
    private Language language = Language.POLSKI;

    private boolean notification = true;

    private boolean proposedNews = true;

    public User() {

    }

    public User(String username, String email,
                String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setDefaultUserSettings(){
        setDarkMode(true);
        setNotification(true);
        setLanguage(Language.POLSKI);
        setProposedNews(true);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<FavouriteTeam> getFavouriteTeams() {
        return favouriteTeams;
    }

    public void setFavouriteTeams(List<FavouriteTeam> favourtiteTeams) {
        this.favouriteTeams = favourtiteTeams;
    }

    public List<UserSite> getUserSites() {
        return userSites;
    }

    public void setUserSites(List<UserSite> userSites) {
        this.userSites = userSites;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isProposedNews() {
        return proposedNews;
    }

    public void setProposedNews(boolean proposedSites) {
        this.proposedNews = proposedSites;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addToRoles(Role role){
        this.roles.add(role);
    }

    public void removeFromRoles(Role role){
        this.roles.remove(role);
    }
}
