package com.project.webApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "not empty")
    private String title;
    private String filename;
    private String description;
    private double rating;
    @OneToMany
    @JoinTable(name = "film_comments",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private List<Comment> comments;
    @ElementCollection(targetClass = Genres.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "film_genres",
            joinColumns = @JoinColumn(name ="film_id"))
    @Enumerated(EnumType.STRING)
    private Set<Genres> genres;
    public Film(){
    }
    public String showRating(){
        String formattedDouble = new DecimalFormat("#0.0").format(rating);
        return formattedDouble;
    }
}
