package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.code.ArticleLikeExceptionCode;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.entity.ArticleLike;
import kakaotech.task4.domain.articleLike.repository.ArticleLikeRepository;
import kakaotech.task4.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;

    public ArticleLikeResponse like(User user, Article article) {
        ArticleLike articleLike = articleLikeRepository.findByArticleAndUser(article, user)
                .orElseGet(() -> createArticleLike(article, user));
        if (!articleLike.isLiked()) {
            articleLike.like();
            article.increaseLikedCount();
        }
        return ArticleLikeResponse.of(true, article.getLikedCount());
    }

    public ArticleLikeResponse unlike(User user, Article article) {
        ArticleLike articleLike = articleLikeRepository.findByArticleAndUser(article, user)
                .orElseThrow(() -> new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND));
        if (!articleLike.isLiked()) {
            throw new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND);
        }
        articleLike.unlike();
        article.decreaseLikedCount();
        return ArticleLikeResponse.of(false, article.getLikedCount());
    }

    public boolean isLiked(User user, Article article) {
        return articleLikeRepository.findByArticleAndUser(article, user)
                .map(ArticleLike::isLiked)
                .orElse(false);
    }

    private ArticleLike createArticleLike(Article article, User user) {
        ArticleLike articleLike = ArticleLike.of(article, user);
        articleLikeRepository.save(articleLike);
        return articleLike;
    }
}