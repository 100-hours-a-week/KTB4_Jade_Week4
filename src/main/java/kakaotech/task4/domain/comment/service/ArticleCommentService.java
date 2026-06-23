package kakaotech.task4.domain.comment.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.dto.res.CommentDetailResponse;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.comment.code.CommentExceptionCode;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.req.UpdateCommentRequest;
import kakaotech.task4.domain.comment.dto.res.CreateCommentResponse;
import kakaotech.task4.domain.comment.entity.ArticleComment;
import kakaotech.task4.domain.comment.repository.ArticleCommentRepository;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ArticleCommentService {
    private final ArticleService articleService;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional
    public CreateCommentResponse createComment(Member member, String articleUuid, CreateCommentRequest request) {
        Article article = articleService.findArticleByUuid(articleUuid);
        ArticleComment articleComment = saveComment(member, article, request);

        article.increaseCommentCount();
        return CreateCommentResponse.from(articleComment.getArticleCommentUuid());
    }

    @Transactional
    public void updateComment(Member member, String articleUuid, String commentUuid, UpdateCommentRequest request) {
        Article article = articleService.findArticleByUuid(articleUuid);
        ArticleComment articleComment = findByCommentUuidAndArticle(commentUuid, article);

        articleComment.validateOwner(member, CommentExceptionCode.FORBIDDEN_UPDATE);
        articleComment.update(request.content());
    }

    @Transactional
    public void deleteComment(Member member, String articleUuid, String commentUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);
        ArticleComment articleComment = findByCommentUuidAndArticle(commentUuid, article);

        articleComment.validateOwner(member, CommentExceptionCode.FORBIDDEN_DELETE);
        articleComment.softDelete();
        article.decreaseCommentCount();
    }

    public List<CommentDetailResponse> findCommentsByArticle(Article article) {
        return articleCommentRepository.findByArticle(article)
                .stream()
                .map(CommentDetailResponse::from)
                .collect(Collectors.toList());
    }

    private ArticleComment saveComment(Member member, Article article, CreateCommentRequest request) {
        String commentUuid = UuidCreator.create(UuidPrefix.COMMENT);
        ArticleComment articleComment = ArticleComment.of(commentUuid, member, article, request);
        articleCommentRepository.save(articleComment);
        return articleComment;
    }

    private ArticleComment findByCommentUuidAndArticle(String commentUuid, Article article) {
        return articleCommentRepository.findByCommentUuidAndArticle(commentUuid, article)
                .orElseThrow(() -> new CustomException(CommentExceptionCode.NOT_FOUND));
    }
}
