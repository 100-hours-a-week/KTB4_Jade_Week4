package kakaotech.task4.domain.articleVote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleVoteCount implements Persistable<Long> {

    @Id
    private Long articleId;

    @Column(nullable = false)
    private int countA = 0;

    @Column(nullable = false)
    private int countB = 0;

    @Transient
    private boolean isNew = true;

    private ArticleVoteCount(Long articleId) {
        this.articleId = articleId;
    }

    public static ArticleVoteCount of(Long articleId) {
        return new ArticleVoteCount(articleId);
    }

    @Override
    public Long getId() {
        return articleId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    private void markNotNew() {
        isNew = false;
    }

    public void increase(VoteOption option) {
        switch (option) {
            case A -> countA++;
            case B -> countB++;
        }
    }

    public void moveTo(VoteOption option) {
        switch (option) {
            case A -> {
                countB = Math.max(0, countB - 1);
                countA++;
            }
            case B -> {
                countA = Math.max(0, countA - 1);
                countB++;
            }
        }
    }
}
