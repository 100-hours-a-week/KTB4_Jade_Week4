package kakaotech.task4.domain.article.entity;

import jakarta.validation.constraints.NotNull;
import kakaotech.task4.common.baseEntity.BaseEntity;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.user.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {

    @Setter
    private int articleId;

    @NotNull
    private String articleUuid;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private String imageUrl;

    private int likedCount = 0;
    private int viewCount = 0;
    private int commentCount = 0;

    @NotNull
    private User user;

    @Builder
    public Article(int articleId, String articleUuid, String title, String content, String imageUrl, User user) {
        this.articleId = articleId;
        this.articleUuid = articleUuid;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public static Article of(String articleUuid, User user, CreateArticleRequest request) {
        return Article.builder()
                .articleUuid(articleUuid)
                .user(user)
                .title(request.title())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .build();
    }

    public synchronized void increaseLikedCount() {
        this.likedCount++;
    }

    public synchronized void decreaseLikedCount() {
        if(this.likedCount > 0) {
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
        if(this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void update(UpdateArticleRequest request) {
        this.title = request.title();
        this.content = request.content();
        updateUpdatedAt();
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