package com.project.webApp.controllers;

import com.project.webApp.models.*;
import com.project.webApp.repository.CommentRepository;
import com.project.webApp.repository.FilmRepository;
import com.project.webApp.repository.UserRepository;
import com.project.webApp.services.FilmService;
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
    public String mainPage(Model model, @AuthenticationPrincipal UserDetails userDetails){
        User authenticatedUser = new User();
        boolean isAuthenticated = false;
        if (userDetails != null) {
            isAuthenticated = true;
//            authenticatedUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        }
        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("isAuthenticated", isAuthenticated);
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
                boolean isDeleted = img.delete();
                System.out.println(isDeleted);
            }
        }

        filmService.deleteFilmFromWatchList(id);
        filmRepository.deleteById(id);
        return "redirect:/films";
    }

    @PostMapping("/films/{id}/add_message")
    public String addMessage(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String message,
                             @PathVariable("id") Long id,
                             Model model) {
        String name = userDetails.getUsername();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Comment comment = new Comment(message, user);
        commentRepository.save(comment);
        filmService.addComment(id, comment.getId());
        return "redirect:/films";
    }

}
