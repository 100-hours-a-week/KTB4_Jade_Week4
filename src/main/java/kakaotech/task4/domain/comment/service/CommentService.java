package kakaotech.task4.domain.comment.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.dto.res.CommentSummaryResponse;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final ArticleService articleService;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public CreateCommentResponse createComment(String userUuid, String articleUuid, CreateCommentRequest request) {
        User user = userService.findByUuid(userUuid, GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        Article article = articleService.findArticleByUuid(articleUuid);
        Comment comment = saveComment(user, article, request);

        article.increaseCommentCount();
        return CreateCommentResponse.from(comment.getCommentUuid());
    }

    public void updateComment(String userUuid, String articleUuid, String commentUuid, UpdateCommentRequest request) {
        Article article = articleService.findArticleByUuid(articleUuid);
        User user = userService.findByUuid(userUuid, GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        Comment comment = findByCommentUuidAndArticle(commentUuid, article);

        comment.validateOwner(user, CommentExceptionCode.FORBIDDEN_UPDATE);
        comment.update(request.content());
    }

    public void deleteComment(String userUuid, String articleUuid, String commentUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);
        User user = userService.findByUuid(userUuid, GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        Comment comment = findByCommentUuidAndArticle(commentUuid, article);

        comment.validateOwner(user, CommentExceptionCode.FORBIDDEN_DELETE);
        comment.softDelete();
        article.decreaseCommentCount();
    }

    public List<CommentSummaryResponse> findCommentsByArticle(Article article) {
        return commentRepository.findByArticle(article)
                .stream()
                .map(CommentSummaryResponse::from)
                .collect(Collectors.toList());
    }

    private Comment saveComment(User user, Article article, CreateCommentRequest request) {
        String commentUuid = UuidCreator.create(UuidPrefix.COMMENT);
        Comment comment = Comment.of(commentUuid, user, article, request);
        commentRepository.save(comment);
        return comment;
    }

    private Comment findByCommentUuidAndArticle(String commentUuid, Article article) {
        return commentRepository.findByCommentUuidAndArticle(commentUuid, article)
                .orElseThrow(() -> new CustomException(CommentExceptionCode.NOT_FOUND));
    }
}
