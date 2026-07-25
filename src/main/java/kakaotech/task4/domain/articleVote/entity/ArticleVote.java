package kakaotech.task4.domain.articleVote.entity;

import jakarta.persistence.*;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_article_vote_member_article",
                        columnNames = {"member_id", "article_id"}
                )
        }
)
public class ArticleVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleVoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private VoteOption voteOption;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ArticleVote(Article article, Member member, VoteOption voteOption) {
        this.article = article;
        this.member = member;
        this.voteOption = voteOption;
    }

    public static ArticleVote of(Article article, Member member, VoteOption voteOption) {
        return ArticleVote.builder()
                .article(article)
                .member(member)
                .voteOption(voteOption)
                .build();
    }

    public boolean changeOptionTo(VoteOption voteOption) {
        if (this.voteOption == voteOption) {
            return false;
        }
        this.voteOption = voteOption;
        return true;
    }
}
