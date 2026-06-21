package kakaotech.task4.domain.comment.entity;

import jakarta.validation.constraints.NotNull;
import kakaotech.task4.common.baseEntity.BaseEntity;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.comment.code.CommentExceptionCode;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.member.entity.Member;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Setter
    private int commentId;

    @NotNull
    private String commentUuid;

    @NotNull
    private String content;

    @NotNull
    private Member member;

    @NotNull
    private Article article;

    @Builder
    public Comment(int commentId, String commentUuid, String content, Member member, Article article) {
        this.commentId = commentId;
        this.commentUuid = commentUuid;
        this.content = content;
        this.member = member;
        this.article = article;
    }

    public static Comment of(String commentUuid, Member member, Article article, CreateCommentRequest request) {
        return Comment.builder()
                .commentUuid(commentUuid)
                .user(member)
                .article(article)
                .content(request.content())
                .build();
    }

    public void validateOwner(Member member, CommentExceptionCode exceptionCode) {
        if (!this.member.equals(member)) {
            throw new CustomException(exceptionCode);
        }
    }

    public void update(String content) {
        this.content = content;
    }

}