package kakaotech.task4.domain.articleVote.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleVote.entity.ArticleVote;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArticleVoteRepository extends JpaRepository<ArticleVote, Long> {

    Optional<ArticleVote> findByArticleAndMember(Article article, Member member);
    List<ArticleVote> findAllByMemberAndArticleIn(Member member, Collection<Article> articles);
}
