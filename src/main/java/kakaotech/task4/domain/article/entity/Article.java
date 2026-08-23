package kakaotech.task4.domain.article.entity;

import jakarta.persistence.*;
import kakaotech.task4.common.baseEntity.BaseEntity;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.member.entity.Member;
import lombok.*;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_article_created_at", columnList = "created_at, article_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @Column(nullable = false, unique = true, updatable = false)
    private String articleUuid;

    @Column(nullable = false, length = 26)
    private String title;

    @Column(nullable = false, length = 15)
    private String optionA;

    @Column(nullable = false, length = 15)
    private String optionB;

    @Column(nullable = false)
    private int likedCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Article(String articleUuid, String title, String optionA, String optionB, Member member) {
        this.articleUuid = articleUuid;
        this.title = title;
        this.optionA = optionA;
        this.optionB = optionB;
        this.member = member;
    }

    public static Article of(String articleUuid, Member member, CreateArticleRequest request) {
        return Article.builder()
                .articleUuid(articleUuid)
                .member(member)
                .title(request.title())
                .optionA(request.optionA())
                .optionB(request.optionB())
                .build();
    }

    public void update(UpdateArticleRequest request) {
        if (request.title() != null) this.title = request.title();
        if (request.optionA() != null) this.optionA = request.optionA();
        if (request.optionB() != null) this.optionB = request.optionB();
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
