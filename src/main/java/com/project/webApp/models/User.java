package com.project.webApp.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @NotEmpty(message = "not empty")
    private String username;
    private String password;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name ="user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    @ManyToMany
    @JoinTable(name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
    private List<User> friends;
    @ElementCollection
    private Map<Film, Integer> watchedFilmList;
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "film_id"}))
    private List<Film> planedFilmList;

    public User(){

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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Map<Film, Integer> getWatchedFilmList() {
        return watchedFilmList;
    }

    public void setWatchedFilmList(Map<Film, Integer> watchedFilmList) {
        this.watchedFilmList = watchedFilmList;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Film> getPlanedFilmList() {
        return planedFilmList;
    }

    public void setPlanedFilmList(List<Film> planedFilmList) {
        this.planedFilmList = planedFilmList;
    }
}
