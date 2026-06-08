package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository {
    void save(Article article);
    void addAll(List<Article> articles);
}
