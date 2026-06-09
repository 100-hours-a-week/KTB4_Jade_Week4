package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository {
    void save(Article article);
    void addAll(List<Article> articles);
    Optional<Article> findByUuid(String articleUuid);
    List<Article> findByCursor(String lastArticleUuid, int size);
}
