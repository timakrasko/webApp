package com.project.webApp.repository;

import com.project.webApp.models.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Long> {

}
