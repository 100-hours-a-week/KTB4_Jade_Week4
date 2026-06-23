package kakaotech.task4.domain.article.entity;

import jakarta.persistence.*;
import kakaotech.task4.common.baseEntity.BaseEntity;
import lombok.*;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_photo_article", columnList = "article_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticlePhoto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articlePhotoId;

    @Column(nullable = false, unique = true, updatable = false)
    private String articlePhotoUuid;

    @Column(nullable = false, length = 512)
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Builder
    public ArticlePhoto(String articlePhotoUuid, String photoUrl, Article article) {
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

    public void updatePhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}