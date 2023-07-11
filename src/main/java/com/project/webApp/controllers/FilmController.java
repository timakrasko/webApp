package com.project.webApp.controllers;

import com.project.webApp.models.Film;
import com.project.webApp.repository.FilmRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class FilmController {
    @Autowired
    private FilmRepository filmRepository;
    @GetMapping("/films")
    public String films(Model model){
        Iterable<Film> films = filmRepository.findAll();
        model.addAttribute("films", films);
        return "films/index";
    }
    @GetMapping("/")
    public String main(Model model){
        Iterable<Film> films = filmRepository.findAll();
        model.addAttribute("films", films);
        return "main";
    }

    @GetMapping("films/{id}")
    public String show(@PathVariable("id") Long id, Model model){
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));
        model.addAttribute("film", film);
        return "films/show";
    }
    @GetMapping("films/new")
    public String newUser(Model model){
        model.addAttribute("film", new Film());
        return "films/new";
    }
    @PostMapping("/films")
    public String create(@ModelAttribute("film") @Valid Film film,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "films/new";
        filmRepository.save(film);
        return "redirect:/films";
    }
    @GetMapping("films/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model){
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Фільм не знайдений"));
        model.addAttribute("film", film);
        return "films/edit";
    }
    @PostMapping("films/{id}")
    public String update(@ModelAttribute("film") @Valid Film film,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "films/edit";
        filmRepository.save(film);
        return "redirect:/films";
    }

    @PostMapping("films/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        filmRepository.deleteById(id);
        return "redirect:/films";
    }


}
