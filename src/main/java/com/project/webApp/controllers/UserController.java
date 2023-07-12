package com.project.webApp.controllers;

import com.project.webApp.models.Film;
import com.project.webApp.models.User;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/users")
public class UserController {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository,
                          UserService userService,
                          FilmRepository filmRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.filmRepository = filmRepository;
    }

    @GetMapping()
    public String index(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "users/index";
    }
    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));
        model.addAttribute("user", user);
        List<User> friends = user.getFriends();
        model.addAttribute("friends", friends);
        List<Film> watcehdfilms = user.getWatchedFilmList();
        model.addAttribute("watchedfilms", watcehdfilms);
        return "users/show";
    }
    @GetMapping("/new")
    public String newUser(Model model){
        model.addAttribute("user", new User());
        return "users/new";
    }
    @PostMapping()
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "users/new";
        userRepository.save(user);
        return "redirect:/users";
    }
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));
        model.addAttribute("user", user);
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
        userRepository.deleteById(id);
        return "redirect:/users";
    }
    @PostMapping("/{friendId}/addfriend")
    public String addFriend(@PathVariable Long friendId,
                            @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("Користувач не знайдений"));
        Long id = user.getId();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Друг не знайдений"));
        if (user.getFriends().contains(friend))
            return "redirect:/users";
        userService.addFriend(id, friendId);
        return "redirect:/users";
    }

    @PostMapping("{filmId}/addtowatchedlist")
    public String addToWatchedList(@PathVariable Long filmId,
                                   @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("Користувач не знайдений"));
        Long id = user.getId();
        Film film = filmRepository.findById(filmId).orElseThrow(()-> new IllegalArgumentException("Фільм не знайдений"));
        if(user.getWatchedFilmList().contains(film))
            return "redirect:/films";
        userService.addFilmToWatchedList(id, filmId);
        return "redirect:/films";
    }

    @GetMapping("/hello")
    public String hello(Model model, @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        model.addAttribute("name", name);
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("Користувач не знайдений"));
        Long id = user.getId();
        return "hello";
    }
}
