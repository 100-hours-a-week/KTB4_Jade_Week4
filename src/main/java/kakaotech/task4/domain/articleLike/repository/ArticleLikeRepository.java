package kakaotech.task4.domain.articleLike.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.entity.ArticleLike;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository {
    void save(ArticleLike articleLike);
    Optional<ArticleLike> findByArticleAndUser(Article article, Member member);
}
