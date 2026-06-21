package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.code.ArticleLikeExceptionCode;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.entity.ArticleLike;
import kakaotech.task4.domain.articleLike.repository.ArticleLikeRepository;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;

    public ArticleLikeResponse like(Member member, Article article) {
        ArticleLike articleLike = articleLikeRepository.findByArticleAndUser(article, member)
                .orElseGet(() -> createArticleLike(article, member));
        if (!articleLike.isLiked()) {
            articleLike.like();
            article.increaseLikedCount();
        }
        return ArticleLikeResponse.of(true, article.getLikedCount());
    }

    public ArticleLikeResponse unlike(Member member, Article article) {
        ArticleLike articleLike = articleLikeRepository.findByArticleAndUser(article, member)
                .orElseThrow(() -> new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND));
        if (!articleLike.isLiked()) {
            throw new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND);
        }
        articleLike.unlike();
        article.decreaseLikedCount();
        return ArticleLikeResponse.of(false, article.getLikedCount());
    }

    public boolean isLiked(Member member, Article article) {
        return articleLikeRepository.findByArticleAndUser(article, member)
                .map(ArticleLike::isLiked)
                .orElse(false);
    }

    private ArticleLike createArticleLike(Article article, Member member) {
        ArticleLike articleLike = ArticleLike.of(article, member);
        articleLikeRepository.save(articleLike);
        return articleLike;
    }
}