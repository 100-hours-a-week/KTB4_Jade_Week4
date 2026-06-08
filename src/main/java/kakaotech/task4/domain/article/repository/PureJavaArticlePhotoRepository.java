package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.ArticlePhoto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
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

}
