package com.project.webApp.repository;

import com.project.webApp.models.Film;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitleContainsIgnoreCase (String title);

    @Query(value = "SELECT AVG(watched_film_list) FROM user_watched_film_list WHERE watched_film_list_key = :id", nativeQuery = true)
    double getAverageFilmRating(@Param("id")Long id);

    @Query(value = "DELETE FROM user_watched_film_list WHERE watched_film_list_key = :id", nativeQuery = true)
    void deleteFilmFromWatchedList(@Param("id") Long id);
}
