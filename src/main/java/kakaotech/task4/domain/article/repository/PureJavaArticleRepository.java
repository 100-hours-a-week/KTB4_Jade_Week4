package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PureJavaArticleRepository implements ArticleRepository {
    private final List<Article> articles = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public void save(Article article) {
        article.setArticleId(sequence.getAndIncrement());
        articles.add(article);
    }

    @Override
    public void addAll(List<Article> articles) {
        this.articles.addAll(articles);
    }

    @Override
    public Optional<Article> findByUuid(String articleUuid) {
        return articles.stream()
                .filter(article -> article.getArticleUuid().equals(articleUuid))
                .findFirst();
    }
}
