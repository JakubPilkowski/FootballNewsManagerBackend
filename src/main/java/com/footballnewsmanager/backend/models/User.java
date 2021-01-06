package com.footballnewsmanager.backend.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.footballnewsmanager.backend.api.request.auth.ValidationMessage;
import com.footballnewsmanager.backend.views.Views;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
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

    @NotNull(message = ValidationMessage.DATE_NOT_BLANK)
    private LocalDateTime addedDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore()
    private List<UserTeam> userTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore()
    private List<UserSite> userSites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore()
    private List<UserNews> userNews = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonView(Views.Internal.class)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


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

    public List<UserTeam> getUserTeams() {
        return userTeams;
    }

    public void setUserTeams(List<UserTeam> userTeams) {
        this.userTeams = userTeams;
    }

    public List<UserSite> getUserSites() {
        return userSites;
    }

    public void setUserSites(List<UserSite> userSites) {
        this.userSites = userSites;
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

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public List<UserNews> getUserNews() {
        return userNews;
    }

    public void setUserNews(List<UserNews> userNews) {
        this.userNews = userNews;
    }
}
