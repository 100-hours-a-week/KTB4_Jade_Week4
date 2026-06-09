package kakaotech.task4.domain.comment.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.comment.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class PureJavaCommentRepository implements CommentRepository {
    private final List<Comment> comments = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public void save(Comment comment) {
        comment.setCommentId(sequence.getAndIncrement());
        comments.add(comment);
    }

    @Override
    public void addAll(List<Comment> comments) {
        this.comments.addAll(comments);
    }

    @Override
    public Optional<Comment> findByCommentUuidAndArticle(String commentUuid, Article article) {
        return comments.stream()
                .filter(comment -> comment.getCommentUuid().equals(commentUuid))
                .filter(comment -> comment.getArticle().equals(article))
                .filter(comment -> !comment.isDeleted())
                .findFirst();
    }

    @Override
    public List<Comment> findByArticle(Article article) {
        return comments.stream()
                .filter(comment -> comment.getArticle().equals(article))
                .filter(comment -> !comment.isDeleted())
                .collect(Collectors.toList());
    }
}
