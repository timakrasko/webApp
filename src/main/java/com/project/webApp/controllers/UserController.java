package com.project.webApp.controllers;

import com.project.webApp.models.Film;
import com.project.webApp.models.User;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
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
    @PostMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable Long id, @PathVariable Long friendId){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Друг не знайдений"));
        if (user.getFriends().contains(friend))
            return "redirect:/users";
        userService.addFriend(id, friendId);
        return "redirect:/users";
    }

//    @PostMapping("/{id}/addFilm")
//    public String addFilm(@PathVariable Long filmId,
//                          Model model){
//        return "redirect:/users";
//    }
}
