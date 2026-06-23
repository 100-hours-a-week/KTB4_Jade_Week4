package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.article.entity.Article;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ArticleSummaryResponse(
        String articleUuid,
        String title,
        int likeCount,
        int commentCount,
        int viewCount,
        LocalDateTime createdAt,
        String writer,
        String profileImageUrl
) {
    public static ArticleSummaryResponse from(Article article) {
        return ArticleSummaryResponse.builder()
                .articleUuid(article.getArticleUuid())
                .title(article.getTitle())
                .likeCount(article.getLikedCount())
                .commentCount(article.getCommentCount())
                .viewCount(article.getViewCount())
                .createdAt(article.getCreatedAt())
                .writer(article.getMember().getNickname())
                .profileImageUrl(article.getMember().getProfileImageUrl())
                .build();
    }
}