package com.project.webApp.controllers;

import com.project.webApp.models.*;
import com.project.webApp.repository.CommentRepository;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.FilmService;
import com.project.webApp.services.UserService;
import jakarta.servlet.http.HttpSession;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    @GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal UserDetails userDetails){
        Iterable<Film> films = filmRepository.findAll();
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
    public String newFilm(Model model){
        model.addAttribute("film", new Film());
        model.addAttribute("genres", Genres.values());
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
    public String edit(@PathVariable("id") Long id, Model model, HttpSession session){
        Film film = filmRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Film not found"));

        String fileName = film.getFilename();
        session.setAttribute("fileName", fileName);

        model.addAttribute("film", film);
        model.addAttribute("genres", Genres.values());
        return "films/edit";
    }
    @PostMapping("films/{id}")
    public String update(@ModelAttribute("film") @Valid Film film,
                         HttpSession session,
                         @RequestParam("file") MultipartFile file,
                         BindingResult bindingResult, Model model) throws IOException {
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

        if (film.getFilename() == null) {
            film.setFilename((String) session.getAttribute("fileName"));
        } else {
            File img = new File(uploadPath + "/" + session.getAttribute("fileName"));
            if (img.exists()) {
                img.delete();
            }
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

        filmService.deleteFilmFromWatchList(id);
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

    @PostMapping("/films/{id}/edit_comment")
    public String editComment(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam String message,
                              @PathVariable("id") Long id,
                              Model model) {

        return "redirect:/films/" + id;
    }

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
