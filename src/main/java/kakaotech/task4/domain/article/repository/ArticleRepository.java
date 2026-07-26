package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("""
        select a from Article a
        join fetch a.member
        where a.articleUuid = :articleUuid
          and a.deletedAt is null
        """)
    Optional<Article> findByArticleUuid(String articleUuid);

    @Query("""
        select a from Article a
        join fetch a.member
        where a.deletedAt is null
        order by a.createdAt desc, a.articleId desc
        """)
    List<Article> findFirstPage(Pageable pageable);

    @Query("""
    select a from Article a
    join fetch a.member
    where a.deletedAt is null
      and (a.createdAt < :cursorCreatedAt
           or (a.createdAt = :cursorCreatedAt and a.articleId < :cursorArticleId))
    order by a.createdAt desc, a.articleId desc
    """)
    List<Article> findNextPage(@Param("cursorCreatedAt") Instant cursorCreatedAt,
                               @Param("cursorArticleId") Long cursorArticleId,
                               Pageable pageable);

    @Modifying
    @Query("update Article a set a.likedCount = a.likedCount + 1 where a.articleId = :id")
    int increaseLikedCount(@Param("id") Long id);

    @Modifying
    @Query("update Article a set a.likedCount = a.likedCount - 1 " +
            "where a.articleId = :id and a.likedCount > 0")
    int decreaseLikedCount(@Param("id") Long id);

    /**
     * 벌크 UPDATE 직후의 카운트를 읽기 위한 스칼라 조회.
     * 엔티티가 아니라 컬럼 하나만 뽑으므로 1차 캐시의 이전 값이 반환되지 않는다.
     */
    @Query("select a.likedCount from Article a where a.articleId = :id")
    int findLikedCount(@Param("id") Long id);

}