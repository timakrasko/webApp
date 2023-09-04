package com.project.webApp.repository;

import com.project.webApp.models.Film;
import com.project.webApp.models.Genres;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByTitleContainsIgnoreCase (String title);
    List<Film> findAllByTitleContainsOrderByRatingDesc(String title);
    List<Film> findAllByTitleContainsOrderByReleaseDateDesc(String title);
    List<Film> findAllByGenresContainsOrderByRatingDesc(Genres genres);
    List<Film> findAllByGenresContainsOrderByReleaseDateDesc(Genres genres);

    @Query(value = "SELECT COUNT(*) FROM user_watched_film_list WHERE watched_film_list_key = :id", nativeQuery = true)
    int checkIfFilmExistsInWatchedList(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM user_watched_film_list WHERE watched_film_list_key = :id", nativeQuery = true)
    int checkIfFilmRateExistsInWatchedList(@Param("id") Long id);

    @Query(value = "SELECT AVG(watched_film_list) FROM user_watched_film_list WHERE watched_film_list_key = :id and watched_film_list != -1", nativeQuery = true)
    double getAverageFilmRating(@Param("id")Long id);

    @Query(value = "DELETE FROM user_watched_film_list WHERE watched_film_list_key = :id", nativeQuery = true)
    void deleteFilmFromWatchedList(@Param("id") Long id);
}
