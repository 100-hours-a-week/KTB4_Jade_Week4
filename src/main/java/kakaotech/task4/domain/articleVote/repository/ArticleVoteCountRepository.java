package kakaotech.task4.domain.articleVote.repository;

import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleVoteCountRepository extends JpaRepository<ArticleVoteCount, Long> {

    @Modifying
    @Query("update ArticleVoteCount c set c.countA = c.countA + 1 where c.articleId = :id")
    void increaseCountA(@Param("id") Long id);

    @Modifying
    @Query("update ArticleVoteCount c set c.countB = c.countB + 1 where c.articleId = :id")
    void increaseCountB(@Param("id") Long id);

    @Modifying
    @Query("update ArticleVoteCount c set c.countA = c.countA + 1, c.countB = c.countB - 1 " +
            "where c.articleId = :id and c.countB > 0")
    void moveVoteFromBToA(@Param("id") Long id);

    @Modifying
    @Query("update ArticleVoteCount c set c.countB = c.countB + 1, c.countA = c.countA - 1 " +
            "where c.articleId = :id and c.countA > 0")
    void moveVoteFromAToB(@Param("id") Long id);
}
