package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
        order by a.createdAt desc
        """)
    List<Article> findFirstPage(Pageable pageable);

    @Query("""
        select a from Article a
        join fetch a.member
        where a.deletedAt is null
          and a.createdAt < :cursorCreatedAt
        order by a.createdAt desc
        """)
    List<Article> findNextPage(@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
                               Pageable pageable);
}