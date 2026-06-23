package kakaotech.task4.domain.comment.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.comment.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    @Query("""
            select c from ArticleComment c
            where c.articleCommentUuid = :commentUuid
              and c.article = :article
              and c.deletedAt is null
            """)
    Optional<ArticleComment> findByCommentUuidAndArticle(@Param("commentUuid") String commentUuid,
                                                         @Param("article") Article article);

    @Query("""
            select c from ArticleComment c
            join fetch c.member
            where c.article = :article
              and c.deletedAt is null
            order by c.createdAt desc
            """)
    List<ArticleComment> findByArticle(@Param("article") Article article);
}