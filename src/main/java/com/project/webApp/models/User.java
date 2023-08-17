package com.project.webApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,
            nullable = false)
    @NotEmpty
//    @Length(min = 2, max = 20, message = "Wrong User name length")
    @Size(min = 2, max= 30, message = "Size of user name should be in range between 2 and 30")
    private String username;

    @Column(unique = true,
            nullable = false)
    @NotEmpty
//    @Length(min = 2, max = 30, message = "Wrong email length")
    @Size(max= 40, message = "Size of email should be lower than 40")
    private String email;

    @Column(nullable = false)
    @NotEmpty
    private String password;

//    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_role",
//            joinColumns = @JoinColumn(name ="user_id"))
    @Enumerated(EnumType.STRING)
    private Role role;

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

    private Boolean locked = false;
    private Boolean enabled = false;

    public User(){
        friends = new ArrayList<>();
        watchedFilmList = new HashMap<>();
        planedFilmList = new ArrayList<>();
    }

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        friends = new ArrayList<>();
        watchedFilmList = new HashMap<>();
        planedFilmList = new ArrayList<>();
    }

    public Boolean isLocked() {
        return locked;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
