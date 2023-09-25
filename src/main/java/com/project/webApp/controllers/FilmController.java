package com.project.webApp.controllers;

import com.project.webApp.models.*;
import com.project.webApp.repository.CommentRepository;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.FilmService;
import com.project.webApp.services.UserService;
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
    private final UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/films")
    public String films(@RequestParam(required = false, defaultValue = "") String filter,
                        Model model, @AuthenticationPrincipal UserDetails userDetails){
        userService.addStartAttributesToModel(model, userDetails);

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
    @GetMapping("/main")
    public String mainPage(Model model,
                           @RequestParam(name = "choose", defaultValue = "1") Integer choose,
                           @AuthenticationPrincipal UserDetails userDetails){
        userService.addStartAttributesToModel(model, userDetails);
        User authenticatedUser = new User();
        boolean isAuthenticated = userDetails != null;
        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("genres", Genres.values());
        filmRepository.findAll();
        Iterable<Film> films = switch (choose) {
            case 1 -> filmRepository.findAllByTitleContainsOrderByRatingDesc("");
            case 2 -> filmRepository.findAllByTitleContainsOrderByReleaseDateDesc("");
            default -> filmRepository.findAll();
        };
        model.addAttribute("films", films);
        return "main";
    }

    @GetMapping("/main/{genre}")
    public String mainPageByGenre(Model model,
                                  @RequestParam(name = "choose", defaultValue = "1") Integer choose,
                                  @PathVariable("genre") Genres genre,
                                  @AuthenticationPrincipal UserDetails userDetails){
        userService.addStartAttributesToModel(model, userDetails);
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
        userService.addStartAttributesToModel(model, userDetails);
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        List<Comment> comments = film.getComments();
        Set<Genres> genres = film.getGenres();
        Integer mark = -1;
        boolean isWatched = false;
        boolean isPlaned = false;

        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            Map<Film, Integer> watchedFilmList = user.getWatchedFilmList();
            isWatched = watchedFilmList.containsKey(film);
            List<Film> planedFilmList = user.getPlanedFilmList();
            isPlaned = planedFilmList.contains(film);
            if (isWatched) {
                mark = watchedFilmList.get(film);
            }
        }

        model.addAttribute("isWatched", isWatched);
        model.addAttribute("isPlaned", isPlaned);
        model.addAttribute("comments", comments);
        model.addAttribute("genres", genres);
        model.addAttribute("mark", mark);
        model.addAttribute("film", film);
        return "films/show";
    }


    @GetMapping("films/new")
    public String newFilm(Model model, @AuthenticationPrincipal UserDetails userDetails){
        userService.addStartAttributesToModel(model, userDetails);
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
    public String edit(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails){
        userService.addStartAttributesToModel(model, userDetails);
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        model.addAttribute("film", film);
        model.addAttribute("genres", Genres.values());
        return "films/edit";
    }
    @PostMapping("films/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("film") @Valid Film updatedFilm,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("date") String date,
                         BindingResult bindingResult) throws IOException, ParseException {
        if (bindingResult.hasErrors())
            return "films/edit";
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
            film.setFilename(resultFilename);
        }
        film.setTitle(updatedFilm.getTitle());
        film.setDescription(updatedFilm.getDescription());
        film.setGenres(updatedFilm.getGenres());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        film.setReleaseDate(dateFormat.parse(date));
        int filmExists = filmService.checkIfFilmExistsInWatchedList(film.getId());
        if (filmExists > 0) {
            film.setRating(filmService.avrRateFilm(film.getId()));
        }
        filmRepository.save(film);
        return "redirect:/films";
    }

    @PostMapping("films/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        Film film = filmRepository.findById(id).orElseThrow();
        if (film.getFilename() != null) {
            File img = new File(uploadPath + "/" + film.getFilename());
            if (img.exists()) {
                img.delete();
            }
        }
        int filmExistsInWatchedList = filmService.checkIfFilmExistsInWatchedList(id);
        int filmExistsInPlanedList = filmService.checkIfFilmExistsInPlanedList(id);
        if (filmExistsInWatchedList > 0) {
            filmService.deleteFilmFromWatchList(id);
        }
        if (filmExistsInPlanedList > 0){
            filmService.deleteFilmFromPlanList(id);
        }
        filmRepository.deleteById(id);
        return "redirect:/films";
    }

    @PostMapping("/films/{id}/add_comment")
    public String addMessage(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String message,
                             @PathVariable("id") Long id,
                             Model model) {
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Comment comment = new Comment(message, user);
        commentRepository.save(comment);
        filmService.addComment(id, comment.getId());
        return "redirect:/films/" + id;
    }

//    @PostMapping("/films/{id}/edit_comment")
//    public String editComment(@AuthenticationPrincipal UserDetails userDetails,
//                              @RequestParam String message,
//                              @PathVariable("id") Long id,
//                              Model model) {
//
//        return "redirect:/films/" + id;
//    }

    @PostMapping("/films/{filmId}/delete_comment/{commentId}")
    public String deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable("filmId") Long filmId,
                                @PathVariable("commentId") Long commentId,
                              Model model) {
        Film film = filmRepository.findById(filmId).orElseThrow();
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        film.getComments().remove(comment);
        filmRepository.save(film);
        commentRepository.delete(comment);
        return "redirect:/films/" + filmId;
    }
}
