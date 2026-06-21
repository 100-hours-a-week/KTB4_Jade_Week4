package kakaotech.task4.domain.articleLike.entity;

import jakarta.validation.constraints.NotNull;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.member.entity.Member;
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
    private Member member;

    @Builder
    public ArticleLike(int articleLikeId, boolean isLiked, Article article, Member member) {
        this.articleLikeId = articleLikeId;
        this.isLiked = isLiked;
        this.article = article;
        this.member = member;
    }

    public static ArticleLike of(Article article, Member member) {
        return ArticleLike.builder()
                .article(article)
                .member(member)
                .isLiked(false)
                .build();
    }

    public void like() {
        this.isLiked = true;
    }

    public void unlike() {
        this.isLiked = false;
    }

    
}