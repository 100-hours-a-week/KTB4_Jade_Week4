package kakaotech.task4.domain.article.entity;

import jakarta.validation.constraints.NotNull;
import kakaotech.task4.common.baseEntity.BaseEntity;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticlePhoto extends BaseEntity {

    @Setter
    private int articlePhotoId;

    @NotNull
    private String articlePhotoUuid;

    @NotNull
    private String photoUrl;

    @NotNull
    private Article article;

    @Builder
    public ArticlePhoto(int articlePhotoId, String articlePhotoUuid, String photoUrl, Article article) {
        this.articlePhotoId = articlePhotoId;
        this.articlePhotoUuid = articlePhotoUuid;
        this.photoUrl = photoUrl;
        this.article = article;
    }

    public static ArticlePhoto of(String articlePhotoUuid, String photoUrl, Article article) {
        return ArticlePhoto.builder()
                .articlePhotoUuid(articlePhotoUuid)
                .photoUrl(photoUrl)
                .article(article)
                .build();
    }
}