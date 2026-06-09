package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.entity.ArticlePhoto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticlePhotoRepository {
    void save(ArticlePhoto articlePhoto);
    Optional<ArticlePhoto> findByArticle(Article article);
}
