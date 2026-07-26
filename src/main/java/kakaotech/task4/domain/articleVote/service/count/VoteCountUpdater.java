package kakaotech.task4.domain.articleVote.service.count;

import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;

public interface VoteCountUpdater {

    ArticleVoteCount increase(Long articleId, VoteOption option);
    ArticleVoteCount moveTo(Long articleId, VoteOption option);
}
