package kakaotech.task4.domain.articleVote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleVoteCount {
    @Id
    private Long articleId;

    @Column(nullable = false)
    private int countA = 0;

    @Column(nullable = false)
    private int countB = 0;

    private ArticleVoteCount(Long articleId) {
        this.articleId = articleId;
    }

    public static ArticleVoteCount of(Long articleId) {
        return new ArticleVoteCount(articleId);
    }
}
