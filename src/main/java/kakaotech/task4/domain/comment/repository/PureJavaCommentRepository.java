package kakaotech.task4.domain.comment.repository;

import kakaotech.task4.domain.comment.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
}
