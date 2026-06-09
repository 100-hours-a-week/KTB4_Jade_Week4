package kakaotech.task4.domain.article.repository;

import kakaotech.task4.domain.article.entity.Article;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                .filter(article -> !article.isDeleted())
                .findFirst();
    }

    @Override
    public List<Article> findByCursor(String lastArticleUuid, int size) {
        List<Article> filtered = getSortedArticles();
        filtered = applyCursor(filtered, lastArticleUuid);
        return filtered.stream()
                .limit(size)
                .collect(Collectors.toList());
    }

    private List<Article> getSortedArticles() {
        return articles.stream()
                .filter(article -> !article.isDeleted())
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    private List<Article> applyCursor(List<Article> articles, String lastArticleUuid) {
        if (lastArticleUuid == null) return articles;
        int index = IntStream.range(0, articles.size())
                .filter(i -> articles.get(i).getArticleUuid().equals(lastArticleUuid))
                .findFirst()
                .orElse(-1);
        return index != -1 ? articles.subList(index + 1, articles.size()) : articles;
    }
}
