package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.comment.entity.ArticleComment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentSummaryResponse(
        String commentUuid,
        String writer,
        String profileImageUrl,
        String userUuid,
        LocalDateTime createdAt,
        String content
) {
    public static CommentSummaryResponse from(ArticleComment articleComment) {
        return CommentSummaryResponse.builder()
                .commentUuid(articleComment.getArticleCommentUuid())
                .writer(articleComment.getMember().getNickname())
                .profileImageUrl(articleComment.getMember().getProfileImageUrl())
                .userUuid(articleComment.getMember().getMemberUuid())
                .createdAt(articleComment.getCreatedAt())
                .content(articleComment.getContent())
                .build();
    }
}