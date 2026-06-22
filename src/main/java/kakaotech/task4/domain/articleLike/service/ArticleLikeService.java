package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.code.ArticleLikeExceptionCode;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.entity.ArticleLike;
import kakaotech.task4.domain.articleLike.repository.ArticleLikeRepository;
import kakaotech.task4.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;

    @Transactional
    public ArticleLikeResponse like(Member member, Article article) {
        if (articleLikeRepository.existsByArticleAndMember(article, member)) {
            throw new CustomException(ArticleLikeExceptionCode.ALREADY_LIKED);
        }
        articleLikeRepository.save(ArticleLike.of(article, member));
        article.increaseLikedCount();
        return ArticleLikeResponse.of(true, article.getLikedCount());
    }

    @Transactional
    public ArticleLikeResponse unlike(Member member, Article article) {
        ArticleLike articleLike = articleLikeRepository.findByArticleAndMember(article, member)
                .orElseThrow(() -> new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND));
        articleLikeRepository.delete(articleLike);
        article.decreaseLikedCount();
        return ArticleLikeResponse.of(false, article.getLikedCount());
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Member member, Article article) {
        return articleLikeRepository.existsByArticleAndMember(article, member);
    }
}