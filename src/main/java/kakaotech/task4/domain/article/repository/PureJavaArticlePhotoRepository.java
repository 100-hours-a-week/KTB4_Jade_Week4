package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.entity.ArticlePhoto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PureJavaArticlePhotoRepository implements ArticlePhotoRepository{
    private final List<ArticlePhoto> articlePhotos = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public void save(ArticlePhoto articlePhoto) {
        articlePhoto.setArticlePhotoId(sequence.getAndIncrement());
        articlePhotos.add(articlePhoto);
    }

    @Override
    public Optional<ArticlePhoto> findByArticle(Article article) {
        return articlePhotos.stream()
                .filter(photo -> photo.getArticle().equals(article))
                .findFirst();
    }

}
