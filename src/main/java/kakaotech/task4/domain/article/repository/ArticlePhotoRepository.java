package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.entity.ArticlePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticlePhotoRepository extends JpaRepository<ArticlePhoto, Long> {

    @Query("""
            select p from ArticlePhoto p
            where p.article = :article
              and p.deletedAt is null
            """)
    Optional<ArticlePhoto> findByArticle(@Param("article") Article article);
}