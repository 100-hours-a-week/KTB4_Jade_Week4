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
        articleLike.like();
        return ArticleLikeResponse.of(articleLike.isLiked(), article.getLikedCount());
    }

    public ArticleLikeResponse unlike(User user, Article article) {
        ArticleLike articleLike = articleLikeRepository.findByArticleAndUser(article, user)
                .orElseThrow(() -> new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND));
        if (!articleLike.isLiked()) {
            throw new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND);
        }
        articleLike.unlike();
        return ArticleLikeResponse.of(articleLike.isLiked(), article.getLikedCount());
    }

    private ArticleLike createArticleLike(Article article, User user) {
        ArticleLike articleLike = ArticleLike.of(article, user);
        articleLikeRepository.save(articleLike);
        return articleLike;
    }
}