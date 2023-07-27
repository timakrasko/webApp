package com.project.webApp.services;

import com.project.webApp.models.Comment;
import com.project.webApp.models.Film;
import com.project.webApp.repository.CommentRepository;
import com.project.webApp.repository.FilmRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {
    private final CommentRepository commentRepository;
    private final FilmRepository filmRepository;
    private final JdbcTemplate jdbcTemplate;

    public FilmService(CommentRepository commentRepository, FilmRepository filmRepository, JdbcTemplate jdbcTemplate) {
        this.commentRepository = commentRepository;
        this.filmRepository = filmRepository;
        this.jdbcTemplate = jdbcTemplate;
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
    public void deleteFilmFromWatchList(Long id) {
        String sql = "DELETE FROM user_watched_film_list WHERE watched_film_list_key = ?";
        jdbcTemplate.update(sql, id);
    }
}
