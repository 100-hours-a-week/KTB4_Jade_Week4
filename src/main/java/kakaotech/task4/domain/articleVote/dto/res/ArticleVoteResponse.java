package kakaotech.task4.domain.articleVote.dto.res;

import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import lombok.Builder;

@Builder
public record ArticleVoteResponse(
        int voteCountA,
        int voteCountB,
        VoteOption myVote,
        boolean changed,
        boolean wasFirst
) {
    public static ArticleVoteResponse of(ArticleVoteCount voteCount,
                                         VoteOption myVote,
                                         boolean changed,
                                         boolean wasFirst) {
        return ArticleVoteResponse.builder()
                .voteCountA(voteCount.getCountA())
                .voteCountB(voteCount.getCountB())
                .myVote(myVote)
                .changed(changed)
                .wasFirst(wasFirst)
                .build();
    }
}
