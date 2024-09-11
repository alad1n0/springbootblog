package edu.itstep.final_project_v1.domain.services;

import edu.itstep.final_project_v1.domain.models.Comment;
import edu.itstep.final_project_v1.domain.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public void save(Comment comment) {
        if (comment.getId() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
        commentRepository.save(comment);
    }
}