package kakaotech.task4.domain.articleLike.entity;

import jakarta.validation.constraints.NotNull;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.user.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleLike {

    @Setter
    private int articleLikeId;

    private boolean isLiked;

    @NotNull
    private Article article;

    @NotNull
    private User user;

    @Builder
    public ArticleLike(int articleLikeId, boolean isLiked, Article article, User user) {
        this.articleLikeId = articleLikeId;
        this.isLiked = isLiked;
        this.article = article;
        this.user = user;
    }

    public static ArticleLike of(Article article, User user) {
        return ArticleLike.builder()
                .article(article)
                .user(user)
                .isLiked(true)
                .build();
    }

    public void like() {
        this.isLiked = true;
    }

    public void unlike() {
        this.isLiked = false;
    }

    
}