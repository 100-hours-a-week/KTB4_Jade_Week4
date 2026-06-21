package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.article.entity.Article;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ArticleDetailResponse(
        String title,
        String writer,
        String userUuid,
        LocalDateTime createdAt,
        String imageUrl,
        String content,
        int likeCount,
        int viewCount,
        int commentCount,
        boolean isLiked,
        List<CommentSummaryResponse> comments
) {
    public static ArticleDetailResponse of(Article article, String imageUrl, boolean isLiked, List<CommentSummaryResponse> comments) {
        return ArticleDetailResponse.builder()
                .title(article.getTitle())
                .writer(article.getMember().getNickname())
                .userUuid(article.getMember().getMemberUuid())
                .createdAt(article.getCreatedAt())
                .imageUrl(imageUrl)
                .content(article.getContent())
                .likeCount(article.getLikedCount())
                .viewCount(article.getViewCount())
                .commentCount(article.getCommentCount())
                .isLiked(isLiked)
                .comments(comments)
                .build();
    }
}