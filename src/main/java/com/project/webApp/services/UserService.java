package com.project.webApp.services;

import com.project.webApp.models.Film;
import com.project.webApp.models.Role;
import com.project.webApp.models.User;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.nio.file.AccessDeniedException;
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
        friend.getFriends().add(user);
        userRepository.save(user);
    }

    public void deleteFriend(Long userId, Long friendId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
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

    public void SetUserLockStatus(Long id, boolean lock, UserDetails userDetails) {
        User admin = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("This account should have ADMIN role");
        }

        User user = userRepository.findById(id).orElseThrow();
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("You can not block/unblock another admin");
        }

        user.setLocked(lock);
        userRepository.save(user);
    }

    public void addStartAttributesToModel(Model model, UserDetails userDetails) {
        boolean isAuthorized = false;
        User user = new User();
        if (userDetails != null) {
            isAuthorized = true;
            user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        }

        boolean isUserLocked = user.isLocked();
        boolean isUserAdmin = user.getRole() == Role.ADMIN;

        model.addAttribute("isAuthenticated", isAuthorized);
        model.addAttribute("isUserLocked", isUserLocked);
        model.addAttribute("isUserAdmin", isUserAdmin);
        model.addAttribute("user", user);
    }
}