package com.project.webApp.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

@Entity
public class User {
    @TableGenerator(
            name = "yourTableGenerator",
            allocationSize = 1,
            initialValue = 1)
    @Id
    @GeneratedValue(
            strategy=GenerationType.TABLE,
            generator="yourTableGenerator")
    private Long id;
    @Column(unique = true)
    @NotEmpty(message = "not empty")
    private String username;
    private String password;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name ="user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    @ManyToMany
    @JoinTable(name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
    private List<User> friends;
    @ManyToMany
    private List<Film> watchedFilmList;


    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }
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

    public List<Film> getWatchedFilmList() {
        return watchedFilmList;
    }

    public void setWatchedFilmList(List<Film> watchedFilmList) {
        this.watchedFilmList = watchedFilmList;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
