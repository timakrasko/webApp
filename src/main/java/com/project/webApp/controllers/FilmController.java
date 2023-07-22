package com.project.webApp.controllers;

import com.project.webApp.models.Film;
import com.project.webApp.models.User;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
public class FilmController {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    @Value("${upload.path}")
    private String uploadPath;

    public FilmController(FilmRepository filmRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/films")
    public String films(@RequestParam(required = false, defaultValue = "") String filter,
                        Model model){
        Iterable<Film> films;
        if (filter != null && !filter.isEmpty()) {
            films = filmRepository.findByTitle(filter);
        } else {
            films = filmRepository.findAll();
        }
        model.addAttribute("films", films);
        model.addAttribute("filter", filter);
        return "films/index";
    }
    @GetMapping("/")
    public String mainPage(Model model){
        Iterable<Film> films = filmRepository.findAll();
        model.addAttribute("films", films);
        return "main";
    }

    @GetMapping("films/{id}")
    public String show(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails){
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        model.addAttribute("film", film);
        String userName = userDetails.getUsername();
        User user = userRepository.findByUsername(userName).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Map<Film, Integer> watchedFilmList = user.getWatchedFilmList();
        boolean isWatched = watchedFilmList.containsKey(film);
        model.addAttribute("isWatched", isWatched);
        Integer mark = -1;
        if (isWatched) {
            mark = watchedFilmList.get(film);
        }
        model.addAttribute("mark", mark);
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
                         @RequestParam("file") MultipartFile file,
                         BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors())
            return "films/edit";
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

    @PostMapping("films/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        filmRepository.deleteById(id);
        return "redirect:/films";
    }

}
