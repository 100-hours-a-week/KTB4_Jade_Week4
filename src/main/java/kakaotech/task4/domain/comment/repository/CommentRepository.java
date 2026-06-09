package kakaotech.task4.domain.comment.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.comment.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository {
    void save(Comment comment);
    void addAll(List<Comment> comments);
    Optional<Comment> findByCommentUuidAndArticle(String commentUuid, Article article);
    List<Comment> findByArticle(Article article);
}
