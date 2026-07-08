package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.articleLike.code.ArticleLikeExceptionCode;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.entity.ArticleLike;
import kakaotech.task4.domain.articleLike.repository.ArticleLikeRepository;
import kakaotech.task4.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleService articleService;

    @Transactional
    public ArticleLikeResponse like(Member member, Article article) {
        int likeCount = article.getLikedCount();
        if (!articleLikeRepository.existsByArticleAndMember(article, member)) {
            articleLikeRepository.save(ArticleLike.of(article, member));
            likeCount = articleService.increaseLikedCount(article.getArticleId());
            log.info("좋아요 눌러써용");
        }
        return ArticleLikeResponse.of(true, likeCount);
    }

    @Transactional
    public ArticleLikeResponse unlike(Member member, Article article) {
        ArticleLike articleLike = articleLikeRepository.findByArticleAndMember(article, member)
                .orElseThrow(() -> new CustomException(ArticleLikeExceptionCode.LIKE_NOT_FOUND));
        articleLikeRepository.delete(articleLike);
        int likeCount = articleService.decreaseLikedCount(article.getArticleId());
        return ArticleLikeResponse.of(false, likeCount);
    }

    public boolean isLiked(Member member, Article article) {
        return articleLikeRepository.existsByArticleAndMember(article, member);
    }
}
