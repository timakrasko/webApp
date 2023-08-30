package com.project.webApp.controllers;

import com.project.webApp.models.*;
import com.project.webApp.repository.CommentRepository;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class FilmController {
    private final FilmRepository filmRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FilmService filmService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/films")
    public String films(@RequestParam(required = false, defaultValue = "") String filter,
                        Model model){
        Iterable<Film> films;
        if (filter != null && !filter.isEmpty()) {
            films = filmRepository.findByTitleContainsIgnoreCase(filter);
        } else {
            films = filmRepository.findAll();
        }
        model.addAttribute("films", films);
        model.addAttribute("filter", filter);
        return "films/index";
    }

    @GetMapping("/")
    public String mainPage(Model model,
                           @RequestParam(name = "choose", defaultValue = "1") Integer choose,
                           @AuthenticationPrincipal UserDetails userDetails){
        User authenticatedUser = new User();
        boolean isAuthenticated = userDetails != null;
        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("genres", Genres.values());
        filmRepository.findAll();
        Iterable<Film> films = switch (choose) {
            case 1 -> filmRepository.findAll();
            case 2 -> filmRepository.findAllByTitleContainsOrderByRating("");
            case 3 -> filmRepository.findAllByTitleContainsOrderByRatingDesc("");
            default -> filmRepository.findAll();
        };
        model.addAttribute("films", films);
        return "main";
    }

    @GetMapping("/{genre}")
    public String mainPageByGenre(Model model,
                                  @RequestParam(name = "choose", defaultValue = "1") Integer choose,
                                  @PathVariable("genre") Genres genre,
                                  @AuthenticationPrincipal UserDetails userDetails){
        User authenticatedUser = new User();
        boolean isAuthenticated = userDetails != null;
        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("genres", Genres.values());
        filmRepository.findAllByGenresContainsOrderByRatingDesc(genre);
        Iterable<Film> films = switch (choose) {
            case 1 -> filmRepository.findAllByGenresContainsOrderByRatingDesc(genre);
            case 2 -> filmRepository.findAllByGenresContainsOrderByReleaseDateDesc(genre);
            default -> filmRepository.findAllByGenresContainsOrderByRatingDesc(genre);
        };
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
        List<Film> planedFilmList = user.getPlanedFilmList();
        boolean isPlaned = planedFilmList.contains(film);
        model.addAttribute("isPlaned", isPlaned);
        List<Comment> comments = film.getComments();
        model.addAttribute("comments", comments);
        Set<Genres> genres = film.getGenres();
        model.addAttribute("genres", genres);
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
        model.addAttribute("genres", Genres.values());
        return "films/new";
    }
    @PostMapping("/films")
    public String create(@ModelAttribute("film") @Valid Film film,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("date") String date,
                         BindingResult bindingResult) throws IOException, ParseException {
        if (bindingResult.hasErrors())
            return "films/new";
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            film.setFilename(resultFilename);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        film.setReleaseDate(dateFormat.parse(date));
        filmRepository.save(film);
        return "redirect:/films";
    }
    @GetMapping("films/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model){
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        filmService.avrRateFilm(id);
        model.addAttribute("film", film);
        model.addAttribute("genres", Genres.values());
        return "films/edit";
    }
    @PostMapping("films/{id}")
    public String update(@ModelAttribute("film") @Valid Film film,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("date") String date,
                         BindingResult bindingResult) throws IOException, ParseException {
        if (bindingResult.hasErrors())
            return "films/edit";
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            film.setFilename(resultFilename);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        film.setReleaseDate(dateFormat.parse(date));
        film.setRating(filmService.avrRateFilm(film.getId()));
        filmRepository.save(film);
        return "redirect:/films";
    }

    @PostMapping("films/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        filmService.deleteFilmFromWatchList(id);
        filmRepository.deleteById(id);
        return "redirect:/films";
    }

    @PostMapping("/films/{id}/add_message")
    public String addMessage(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String message,
                             @PathVariable("id") Long id) {
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Comment comment = new Comment(message, user);
        commentRepository.save(comment);
        filmService.addComment(id, comment.getId());
        return "redirect:/films";
    }

}
