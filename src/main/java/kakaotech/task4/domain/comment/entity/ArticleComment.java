package kakaotech.task4.domain.comment.entity;

import jakarta.persistence.*;
import kakaotech.task4.common.baseEntity.BaseEntity;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.comment.code.CommentExceptionCode;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.member.entity.Member;
import lombok.*;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_comment_article_created", columnList = "article_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleCommentId;

    @Column(nullable = false, unique = true, updatable = false)
    private String articleCommentUuid;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Builder
    public ArticleComment(String articleCommentUuid, String content, Member member, Article article) {
        this.articleCommentUuid = articleCommentUuid;
        this.content = content;
        this.member = member;
        this.article = article;
    }

    public static ArticleComment of(String articleCommentUuid, Member member, Article article, CreateCommentRequest request) {
        return ArticleComment.builder()
                .articleCommentUuid(articleCommentUuid)
                .member(member)
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