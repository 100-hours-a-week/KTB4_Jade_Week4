package kakaotech.task4.domain.article.dto.cursor;

import kakaotech.task4.domain.article.entity.Article;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

public record ArticleCursor(LocalDateTime createdAt, Long articleId) {

    public static String encode(Article article) {
        String raw = article.getCreatedAt() + "|" + article.getArticleId();
        return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static ArticleCursor decode(String cursor) {
        String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        String[] parts = raw.split("\\|");
        return new ArticleCursor(LocalDateTime.parse(parts[0]), Long.parseLong(parts[1]));
    }
}