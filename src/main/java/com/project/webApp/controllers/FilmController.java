package com.project.webApp.controllers;

import com.project.webApp.models.Film;
import com.project.webApp.repository.FilmRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller

public class FilmController {
    private final FilmRepository filmRepository;
    @Value("${upload.path}")
    private String uploadPath;

    public FilmController(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @GetMapping("/films")
    public String films(Model model){
        Iterable<Film> films = filmRepository.findAll();
        model.addAttribute("films", films);
        return "films/index";
    }
    @GetMapping("/")
    public String mainPage(Model model){
        Iterable<Film> films = filmRepository.findAll();
        model.addAttribute("films", films);
        return "main";
    }

    @GetMapping("films/{id}")
    public String show(@PathVariable("id") Long id, Model model){
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        model.addAttribute("film", film);
        return "films/show";
    }
    @GetMapping("films/new")
    public String newFilm(Model model){
        model.addAttribute("film", new Film());
        return "films/new";
    }
    @PostMapping("/films")
    public String create(@ModelAttribute("film") @Valid Film film,
                         @RequestParam("file") MultipartFile file,
                         BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors())
            return "films/new";
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            film.setFilename(resultFilename);
        }
        filmRepository.save(film);
        return "redirect:/films";
    }
    @GetMapping("films/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model){
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));
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
