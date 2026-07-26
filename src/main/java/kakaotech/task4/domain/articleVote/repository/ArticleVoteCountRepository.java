package kakaotech.task4.domain.articleVote.repository;

import jakarta.persistence.LockModeType;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleVoteCountRepository extends JpaRepository<ArticleVoteCount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ArticleVoteCount c where c.articleId = :id")
    Optional<ArticleVoteCount> findByIdForUpdate(@Param("id") Long id);
}
