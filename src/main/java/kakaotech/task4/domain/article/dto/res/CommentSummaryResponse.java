package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.comment.entity.Comment;
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
    public static CommentSummaryResponse from(Comment comment) {
        return CommentSummaryResponse.builder()
                .commentUuid(comment.getCommentUuid())
                .writer(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .userUuid(comment.getUser().getUserUuid())
                .createdAt(comment.getCreatedAt())
                .content(comment.getContent())
                .build();
    }
}