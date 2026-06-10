package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArticleLikeFacadeService {
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;

    public ArticleLikeResponse like(User user, String articleUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);
        article.increaseLikedCount();
        return articleLikeService.like(user, article);
    }

    public ArticleLikeResponse unlike(User user, String articleUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);
        article.decreaseLikedCount();
        return articleLikeService.unlike(user, article);
    }
}