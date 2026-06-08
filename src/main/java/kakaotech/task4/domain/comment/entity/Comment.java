package kakaotech.task4.domain.comment.entity;

import jakarta.validation.constraints.NotNull;
import kakaotech.task4.common.baseEntity.BaseEntity;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.comment.code.CommentExceptionCode;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.user.entity.User;
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
    private User user;

    @NotNull
    private Article article;

    @Builder
    public Comment(int commentId, String commentUuid, String content, User user, Article article) {
        this.commentId = commentId;
        this.commentUuid = commentUuid;
        this.content = content;
        this.user = user;
        this.article = article;
    }

    public static Comment of(String commentUuid, User user, Article article, CreateCommentRequest request) {
        return Comment.builder()
                .commentUuid(commentUuid)
                .user(user)
                .article(article)
                .content(request.content())
                .build();
    }

    public void validateOwner(User user) {
        if (!this.user.equals(user)) {
            throw new CustomException(CommentExceptionCode.FORBIDDEN);
        }
    }

    public void update(String content) {
        this.content = content;
        updateUpdatedAt();
    }

}