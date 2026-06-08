package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.ArticlePhoto;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticlePhotoRepository {
    void save(ArticlePhoto articlePhoto);
}
