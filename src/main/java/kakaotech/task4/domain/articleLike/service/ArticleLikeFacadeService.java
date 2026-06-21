package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArticleLikeFacadeService {
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;

    public ArticleLikeResponse like(Member member, String articleUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);
        return articleLikeService.like(member, article);
    }

    public ArticleLikeResponse unlike(Member member, String articleUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);
        return articleLikeService.unlike(member, article);
    }
}