package com.project.webApp.repository;

import com.project.webApp.models.Film;
import org.springframework.data.repository.CrudRepository;

public interface FilmRepository extends CrudRepository<Film, Long> {

}
