package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.comment.entity.ArticleComment;
import kakaotech.task4.domain.member.entity.Member;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CommentDetailResponse(
        String commentUuid,
        String writer,
        String profileImageUrl,
        boolean isMine,
        Instant createdAt,
        String content
) {
    public static CommentDetailResponse of(ArticleComment articleComment, Member viewer) {
        return CommentDetailResponse.builder()
                .commentUuid(articleComment.getArticleCommentUuid())
                .writer(articleComment.getMember().getNickname())
                .profileImageUrl(articleComment.getMember().getProfileImageUrl())
                .isMine(articleComment.getMember().equals(viewer))
                .createdAt(articleComment.getCreatedAt())
                .content(articleComment.getContent())
                .build();
    }
}