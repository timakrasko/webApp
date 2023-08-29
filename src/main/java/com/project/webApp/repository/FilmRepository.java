package com.project.webApp.repository;

import com.project.webApp.models.Film;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitleContainsIgnoreCase (String title);
    List<Film> findAllByTitleContainsOrderByRatingAsc(String title);
}
