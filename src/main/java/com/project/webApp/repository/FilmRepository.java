package com.project.webApp.repository;

import com.project.webApp.models.Film;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitle(String title);
}
