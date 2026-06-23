package kakaotech.task4.domain.articleLike.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.entity.ArticleLike;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    Optional<ArticleLike> findByArticleAndMember(Article article, Member member);

    boolean existsByArticleAndMember(Article article, Member member);
}