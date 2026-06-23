package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.comment.entity.ArticleComment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDetailResponse(
        String commentUuid,
        String writer,
        String profileImageUrl,
        String userUuid,
        LocalDateTime createdAt,
        String content
) {
    public static CommentDetailResponse from(ArticleComment articleComment) {
        return CommentDetailResponse.builder()
                .commentUuid(articleComment.getArticleCommentUuid())
                .writer(articleComment.getMember().getNickname())
                .profileImageUrl(articleComment.getMember().getProfileImageUrl())
                .userUuid(articleComment.getMember().getMemberUuid())
                .createdAt(articleComment.getCreatedAt())
                .content(articleComment.getContent())
                .build();
    }
}