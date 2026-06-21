package kakaotech.task4.domain.article.entity;

import jakarta.persistence.*;
import kakaotech.task4.common.baseEntity.BaseEntity;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.user.entity.User;
import lombok.*;

@Entity
@Table(name = "article")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "article_uuid", nullable = false, unique = true, updatable = false)
    private String articleUuid;

    @Column(nullable = false, length = 26)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "liked_count", nullable = false)
    private int likedCount = 0;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Article(String articleUuid, String title, String content, User user) {
        this.articleUuid = articleUuid;
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static Article of(String articleUuid, User user, CreateArticleRequest request) {
        return Article.builder()
                .articleUuid(articleUuid)
                .user(user)
                .title(request.title())
                .content(request.content())
                .build();
    }

    public synchronized void increaseLikedCount() {
        this.likedCount++;
    }

    public synchronized void decreaseLikedCount() {
        if (this.likedCount > 0) {
            this.likedCount--;
        }
    }

    public synchronized void increaseViewCount() {
        this.viewCount++;
    }

    public synchronized void increaseCommentCount() {
        this.commentCount++;
    }

    public synchronized void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void update(UpdateArticleRequest request) {
        this.title = request.title();
        this.content = request.content();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return articleUuid != null && articleUuid.equals(article.getArticleUuid());
    }

    @Override
    public int hashCode() {
        return (articleUuid != null) ? articleUuid.hashCode() : 0;
    }
}