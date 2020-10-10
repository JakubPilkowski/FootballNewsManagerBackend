package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.views.Views;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
    private Long id;


    @NotBlank(message = "Login jest wymagany")
    @Size(max = 20)
    private String username;

    @NaturalId(mutable = true)
    @NotBlank(message = "Adres mailowy jest wymagany")
    @Size(max = 40)
    @Email
//    @JsonIgnore
    private String email;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(max = 60)
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavouriteTeam> favouriteTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSite> userSites = new ArrayList<>();


    //w momencie dodawania storage
//    @NotBlank
//    private String imageUrl;


    @ManyToMany(fetch = FetchType.LAZY)
    @JsonView(Views.Internal.class)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    private boolean darkMode = true;

    @Enumerated(EnumType.STRING)
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
