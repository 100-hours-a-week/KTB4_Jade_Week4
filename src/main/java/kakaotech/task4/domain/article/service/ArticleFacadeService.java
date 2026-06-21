package kakaotech.task4.domain.article.service;

import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleDetailResponse;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.CommentSummaryResponse;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.service.ArticleLikeService;
import kakaotech.task4.domain.comment.service.CommentService;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ArticleFacadeService {
    private final ArticleService articleService;
    private final ArticlePhotoService articlePhotoService;
    private final CommentService commentService;
    private final ArticleLikeService articleLikeService;

    public CreateArticleResponse createArticle(Member member, CreateArticleRequest request) {
        Article article = articleService.createArticle(member, request);
        articlePhotoService.savePhoto(request.imageUrl(), article);
        return CreateArticleResponse.from(article.getArticleUuid());
    }

    public void updateArticle(Member member, String articleUuid, UpdateArticleRequest request) {
        Article article = articleService.updateArticle(member, articleUuid, request);
        articlePhotoService.updatePhoto(request.imageUrl(), article);
    }

    public void deleteArticle(Member member, String articleUuid) {
        articleService.deleteArticle(member, articleUuid);
    }

    public ArticleListResponse getArticleList(String lastArticleUuid, int size) {
        return articleService.getArticleList(lastArticleUuid, size);
    }

    public ArticleDetailResponse getArticleDetail(Member member, String articleUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);
        article.increaseViewCount();

        String imageUrl = articlePhotoService.findPhotoUrlByArticle(article);
        boolean isLiked = articleLikeService.isLiked(member, article);
        List<CommentSummaryResponse> comments = commentService.findCommentsByArticle(article);

        return ArticleDetailResponse.of(article, imageUrl, isLiked, comments);
    }
}