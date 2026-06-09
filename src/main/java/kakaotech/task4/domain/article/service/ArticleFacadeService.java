package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleDetailResponse;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.CommentSummaryResponse;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.service.ArticleLikeFacadeService;
import kakaotech.task4.domain.articleLike.service.ArticleLikeService;
import kakaotech.task4.domain.comment.service.CommentService;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ArticleFacadeService {
    private final ArticleService articleService;
    private final ArticlePhotoService articlePhotoService;
    private final CommentService commentService;
    private final UserService userService;
    private final ArticleLikeService articleLikeService;

    public CreateArticleResponse createArticle(String userUuid, CreateArticleRequest request) {
        Article article = articleService.createArticle(userUuid, request);
        articlePhotoService.savePhoto(request.imageUrl(), article);
        return CreateArticleResponse.from(article.getArticleUuid());
    }

    public void updateArticle(String userUuid, String articleUuid, UpdateArticleRequest request) {
        Article article = articleService.updateArticle(userUuid, articleUuid, request);
        articlePhotoService.updatePhoto(request.imageUrl(), article);
    }

    public void deleteArticle(String userUuid, String articleUuid) {
        articleService.deleteArticle(userUuid, articleUuid);
    }

    public ArticleListResponse getArticleList(String lastArticleUuid, int size) {
        return articleService.getArticleList(lastArticleUuid, size);
    }

    public ArticleDetailResponse getArticleDetail(String userUuid, String articleUuid) {
        User user = userService.findByUuid(userUuid, GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        Article article = articleService.findArticleByUuid(articleUuid);
        article.increaseViewCount();

        String imageUrl = articlePhotoService.findPhotoUrlByArticle(article);
        boolean isLiked = articleLikeService.isLiked(user, article);
        List<CommentSummaryResponse> comments = commentService.findCommentsByArticle(article);

        return ArticleDetailResponse.of(article, imageUrl, isLiked, comments);
    }
}