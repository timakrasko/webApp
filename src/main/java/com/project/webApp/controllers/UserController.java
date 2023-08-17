package com.project.webApp.controllers;

import com.project.webApp.models.ConfirmationToken;
import com.project.webApp.models.Film;
import com.project.webApp.models.Role;
import com.project.webApp.models.User;
import com.project.webApp.repository.CommentRepository;
import com.project.webApp.repository.ConfirmationTokenRepository;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.FilmService;
import com.project.webApp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserService userService;
    private final FilmService filmService;

    @GetMapping()
    public String index(Model model, @AuthenticationPrincipal UserDetails userDetails){
        userService.addStartAttributesToModel(model, userDetails);
        model.addAttribute("users", userRepository.findAll());
        return "users/index";
    }
//    @GetMapping("/{id}")
//    public String show(@PathVariable("id") Long id, Model model,
//                       @AuthenticationPrincipal UserDetails userDetails){
////        addStartAttributesToModel(model, userDetails);
//
//        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
//        model.addAttribute("user", user);
//
//        String name = userDetails.getUsername();
//        User autUser = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
//
//        List<User> friendList = autUser.getFriends();
//        boolean isFriend = friendList.contains(user);
//        model.addAttribute("isFriend", isFriend);
//
//        boolean isItself = user.getId().equals(autUser.getId());
//        model.addAttribute("isItself", isItself);
//
//        List<User> friends = user.getFriends();
//        model.addAttribute("friends", friends);
//
//        Map<Film, Integer> watchedFilmList = user.getWatchedFilmList();
//        model.addAttribute("watchedFilms", watchedFilmList);
//
//        List<Film> planedFilmList = user.getPlanedFilmList();
//        model.addAttribute("planedFilms", planedFilmList);
//
//        boolean isAutUserAdmin = autUser.getRole() == Role.ADMIN;
//        model.addAttribute("isAutUserAdmin", isAutUserAdmin);
//
//        boolean isUserLocked = user.isLocked();
//        model.addAttribute("isUserLocked", isUserLocked);
//
//        boolean isUserAdmin = user.getRole() == Role.ADMIN;
//        model.addAttribute("isUserAdmin", isUserAdmin);
//        return "users/show";
//    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal UserDetails userDetails){
        userService.addStartAttributesToModel(model, userDetails);

        User anotherUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("anotherUser", anotherUser);

        User user = new User();
        if (userDetails != null) {
            user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        }

        List<User> friendList = user.getFriends();
        boolean isFriend = friendList.contains(anotherUser);
        model.addAttribute("isFriend", isFriend);

        boolean isItself = anotherUser.getId().equals(user.getId());
        model.addAttribute("isItself", isItself);

        List<User> friends = anotherUser.getFriends();
        model.addAttribute("friends", friends);

        Map<Film, Integer> watchedFilmList = anotherUser.getWatchedFilmList();
        model.addAttribute("watchedFilms", watchedFilmList);

        List<Film> planedFilmList = anotherUser.getPlanedFilmList();
        model.addAttribute("planedFilms", planedFilmList);

        boolean isUserLocked = anotherUser.isLocked();
        model.addAttribute("isAnotherUserLocked", isUserLocked);

        boolean isUserAdmin = anotherUser.getRole() == Role.ADMIN;
        model.addAttribute("isAnotherUserAdmin", isUserAdmin);
        return "users/show";
    }

    @GetMapping("/test")
    public String hello(Model model) {
        User user = new User();
        model.addAttribute("id", user.getId());
        return "hello";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "users/edit";
    }
    @PostMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "users/edit";
        userRepository.save(user);
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        confirmationTokenRepository.deleteByUser_id(id);
        userRepository.deleteById(id);
        return "redirect:/users";
    }
    @PostMapping("/{friendId}/add_friend")
    public String addFriend(@PathVariable Long friendId,
                            @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        if (user.getFriends().contains(friend))
            return "redirect:/users/" + friendId;
        userService.addFriend(id, friendId);
        return "redirect:/users/" + friendId;
    }

    @PostMapping("/{friendId}/delete_friend")
    public String deleteFriend(@PathVariable Long friendId,
                            @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        if (!user.getFriends().contains(friend))
            return "redirect:/users/" + friendId;
        userService.deleteFriend(id, friendId);
        return "redirect:/users/" + friendId;
    }

    @PostMapping("{filmId}/add_to_watched_list")
    public String addToWatchedList(@PathVariable Long filmId,
                                   @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        Film film = filmRepository.findById(filmId).orElseThrow(()-> new IllegalArgumentException("Film not found"));
        if(user.getWatchedFilmList().containsKey(film))
            return "redirect:/films";
        userService.addFilmToWatchedList(id, filmId);
        return "redirect:/films/" + filmId;
    }

    @PostMapping("{filmId}/delete_from_watched_list")
    public String deleteFromWatchedList(@PathVariable Long filmId,
                                   @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        Film film = filmRepository.findById(filmId).orElseThrow(()-> new IllegalArgumentException("Film not found"));
        if(!user.getWatchedFilmList().containsKey(film))
            return "redirect:/films";
        userService.deleteFilmFromWatchedList(id, filmId);
        return "redirect:/films/" + filmId;
    }
    @PostMapping("{filmId}/add_to_planed_list")
    public String addToPlanedList(@PathVariable Long filmId,
                                   @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("Користувач не знайдений"));
        Long id = user.getId();
        Film film = filmRepository.findById(filmId).orElseThrow(()-> new IllegalArgumentException("Фільм не знайдений"));
        if(user.getPlanedFilmList().contains(film))
            return "redirect:/films";
        userService.addFilmToPlanedList(id, filmId);
        return "redirect:/films/" + filmId;
    }
    @PostMapping("{filmId}/delete_from_planed_list")
    public String deleteFromPlanedList(@PathVariable Long filmId,
                                  @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("Користувач не знайдений"));
        Long id = user.getId();
        Film film = filmRepository.findById(filmId).orElseThrow(()-> new IllegalArgumentException("Фільм не знайдений"));
        if(!user.getPlanedFilmList().contains(film))
            return "redirect:/films";
        userService.deleteFilmFromPlanedList(id, filmId);
        return "redirect:/films/" + filmId;
    }

    @PostMapping("/{filmId}/rate_film")
    public String rateFilm(@PathVariable Long filmId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam("param") int value){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Film film = filmRepository.findById(filmId).orElseThrow(()-> new IllegalArgumentException("Film not found"));
        Long id = user.getId();
        userService.rateFilm(id, filmId, value);
        film.setRating(filmService.avrRateFilm(filmId));
        filmRepository.save(film);
        return "redirect:/films/" + filmId;
    }

    @PostMapping("/{id}/block")
    public String blockUser(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.SetUserLockStatus(id, true, userDetails);
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/unblock")
    public String unblockUser(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.SetUserLockStatus(id, false, userDetails);
        return "redirect:/users/" + id;
    }

//    public void addStartAttributesToModel(Model model, UserDetails userDetails) {
//        boolean isAuthorized = false;
//        User user = new User();
//        if (userDetails != null) {
//            isAuthorized = true;
//            user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
//        }
//
//        boolean isUserLocked = user.isLocked();
//        boolean isUserAdmin = user.getRole() == Role.ADMIN;
//
//        model.addAttribute("isAuthenticated", isAuthorized);
//        model.addAttribute("isUserLocked", isUserLocked);
//        model.addAttribute("isUserAdmin", isUserAdmin);
//        model.addAttribute("user", user);
//    }
}
