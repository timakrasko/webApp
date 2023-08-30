package com.project.webApp.repository;

import com.project.webApp.models.Film;
import com.project.webApp.models.Genres;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitleContainsIgnoreCase (String title);
    List<Film> findAllByTitleContainsOrderByRatingDesc(String title);
    List<Film> findAllByTitleContainsOrderByRating(String title);
    List<Film> findAllByGenresContainsOrderByRatingDesc(Genres genres);
    List<Film> findAllByGenresContainsOrderByReleaseDateDesc(Genres genres);
}
