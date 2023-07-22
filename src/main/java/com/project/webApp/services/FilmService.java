package com.project.webApp.services;

import com.project.webApp.models.Comment;
import com.project.webApp.models.Film;
import com.project.webApp.repository.CommentRepository;
import com.project.webApp.repository.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {
    private final CommentRepository commentRepository;
    private final FilmRepository filmRepository;

    public FilmService(CommentRepository commentRepository, FilmRepository filmRepository) {
        this.commentRepository = commentRepository;
        this.filmRepository = filmRepository;
    }
    public void addComment(Long filmId, Long commentId){
        Film film = filmRepository.findById(filmId).orElse(null);
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (film != null & comment != null){
            List<Comment> commentList = film.getComments();
            commentList.add(comment);
            filmRepository.save(film);
        }
    }
}
