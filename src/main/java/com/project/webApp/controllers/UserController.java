package com.project.webApp.controllers;

import com.project.webApp.models.Film;
import com.project.webApp.models.Role;
import com.project.webApp.models.User;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {
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
    public String show(@PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal UserDetails userDetails){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        String name = userDetails.getUsername();
        User autUser = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        List<User> friendList = autUser.getFriends();
        boolean isFriend = friendList.contains(user);
        model.addAttribute("isFriend", isFriend);
        boolean isItself = user.getId().equals(autUser.getId());
        model.addAttribute("isItself", isItself);
        List<User> friends = user.getFriends();
        model.addAttribute("friends", friends);
        Map<Film, Integer> watchedFilmList = user.getWatchedFilmList();
        model.addAttribute("watchedfilms", watchedFilmList);
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
        userRepository.deleteById(id);
        return "redirect:/users";
    }
    @PostMapping("/{friendId}/addfriend")
    public String addFriend(@PathVariable Long friendId,
                            @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        if (user.getFriends().contains(friend))
            return "redirect:/users";
        userService.addFriend(id, friendId);
        return "redirect:/users";
    }

    @PostMapping("/{friendId}/deletefriend")
    public String deleteFriend(@PathVariable Long friendId,
                            @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        if (!user.getFriends().contains(friend))
            return "redirect:/users";
        userService.deleteFriend(id, friendId);
        return "redirect:/users";
    }

    @PostMapping("{filmId}/addtowatchedlist")
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

    @PostMapping("{filmId}/deletefromwatchedlist")
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

    @PostMapping("/{filmId}/ratefilm")
    public String rateFilm(@PathVariable Long filmId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam("param") int value){
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        userService.raitFilm(id, filmId, value);
        return "redirect:/films/" + filmId;
    }

    @GetMapping("/hello")
    public String hello(Model model, @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        model.addAttribute("name", name);
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Long id = user.getId();
        return "hello";
    }
}
