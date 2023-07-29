package com.project.webApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

@Entity
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<Genres> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genres> genres) {
        this.genres = genres;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
