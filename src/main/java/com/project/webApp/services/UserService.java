package com.project.webApp.services;

import com.project.webApp.models.Film;
import com.project.webApp.models.User;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public UserService(UserRepository userRepository, FilmRepository filmRepository) {
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        user.getFriends().add(friend);
        userRepository.save(user);
    }
    public void addFilmToWatchedList(Long userId, Long filmId) {
        User user = userRepository.findById(userId).orElse(null);
        Film movie = filmRepository.findById(filmId).orElse(null);

        if (user != null && movie != null) {
            List<Film> watchedMovies = user.getWatchedFilmList();
            watchedMovies.add(movie);
            userRepository.save(user);
        }
    }
}