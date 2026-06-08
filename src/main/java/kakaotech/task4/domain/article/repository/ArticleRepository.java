package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository {
    void save(Article article);

}
