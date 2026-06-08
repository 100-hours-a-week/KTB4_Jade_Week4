package kakaotech.task4.domain.comment.repository;

import kakaotech.task4.domain.comment.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository {
    void save(Comment comment);
    void addAll(List<Comment> comments);
}
