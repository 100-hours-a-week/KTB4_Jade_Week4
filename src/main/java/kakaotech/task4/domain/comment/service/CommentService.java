package kakaotech.task4.domain.comment.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.comment.code.CommentExceptionCode;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.req.UpdateCommentRequest;
import kakaotech.task4.domain.comment.dto.res.CreateCommentResponse;
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

    public void updateComment(String userUuid, String articleUuid, String commentUuid, UpdateCommentRequest request) {
        Article article = articleService.findArticleByUuid(articleUuid);
        User user = findUserByUuid(userUuid);
        Comment comment = findByCommentUuidAndArticle(commentUuid, article);

        comment.validateOwner(user);
        comment.update(request.content());
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

    private Comment findByCommentUuidAndArticle(String commentUuid, Article article) {
        return commentRepository.findByCommentUuidAndArticle(commentUuid, article)
                .orElseThrow(() -> new CustomException(CommentExceptionCode.NOT_FOUND));
    }
}
