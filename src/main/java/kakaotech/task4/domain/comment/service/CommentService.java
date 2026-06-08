package kakaotech.task4.domain.comment.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.comment.dto.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.CreateCommentResponse;
import kakaotech.task4.domain.comment.entity.Comment;
import kakaotech.task4.domain.comment.repository.CommentRepository;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private final ArticleService articleService;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public CreateCommentResponse createComment(String userUuid, String articleUuid, CreateCommentRequest request) {
        User user = findUserByUuid(userUuid);
        Article article = articleService.findArticleByUuid(articleUuid);
        Comment comment = saveComment(user, article, request);

        article.increaseCommentCount();
        return CreateCommentResponse.from(comment.getCommentUuid());
    }

    private Comment saveComment(User user, Article article, CreateCommentRequest request) {
        String commentUuid = UuidCreator.create(UuidPrefix.COMMENT);
        Comment comment = Comment.of(commentUuid, user, article, request);
        commentRepository.save(comment);
        return comment;
    }

    private User findUserByUuid(String userUuid) {
        return userService.findByUuid(userUuid)
                .orElseThrow(() -> new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR));
    }
}
