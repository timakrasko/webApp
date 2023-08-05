package com.project.webApp.services;

import com.project.webApp.models.Film;
import com.project.webApp.models.User;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public void addFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));
        user.getFriends().add(friend);
        userRepository.save(user);
    }

    public void deleteFriend(Long userId, Long friendId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));
        user.getFriends().remove(friend);
        userRepository.save(user);
    }

    public void addFilmToWatchedList(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);
        if (user.getPlanedFilmList().contains(film))
            deleteFilmFromPlanedList(userId, filmId);
        if (user != null && film != null) {
            Map<Film, Integer> watchedFilms = user.getWatchedFilmList();
            watchedFilms.put(film, -1);
            userRepository.save(user);
        }
    }

    public void deleteFilmFromWatchedList(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);
        if (user != null && film != null) {
            Map<Film, Integer> watchedFilms = user.getWatchedFilmList();
            watchedFilms.remove(film);
            userRepository.save(user);
        }
    }
    public void addFilmToPlanedList(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        if (user != null && film != null) {
            List<Film> watchedMovies = user.getPlanedFilmList();
            watchedMovies.add(film);
            userRepository.save(user);
        }
    }
    public void deleteFilmFromPlanedList(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);

        if (user != null && film != null) {
            List<Film> watchedMovies = user.getPlanedFilmList();
            watchedMovies.remove(film);
            userRepository.save(user);
        }
    }
    public void rateFilm (Long userId, Long filmId, Integer value){
        User user = userRepository.findById(userId).orElse(null);
        Film film = filmRepository.findById(filmId).orElse(null);
        if (user.getPlanedFilmList().contains(film))
            deleteFilmFromPlanedList(userId, filmId);
        if (user != null && film != null) {
            Map<Film, Integer> watchedFilms = user.getWatchedFilmList();
            watchedFilms.put(film, value);
            userRepository.save(user);
        }
    }


}