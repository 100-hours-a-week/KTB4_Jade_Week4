package kakaotech.task4.domain.articleVote.service.count;

import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class PessimisticLockVoteCountUpdater implements VoteCountUpdater {
    private final ArticleVoteCountRepository articleVoteCountRepository;

    @Override
    public ArticleVoteCount increase(Long articleId, VoteOption option) {
        ArticleVoteCount voteCount = lock(articleId);
        voteCount.increase(option);
        return voteCount;
    }

    @Override
    public ArticleVoteCount moveTo(Long articleId, VoteOption option) {
        ArticleVoteCount voteCount = lock(articleId);
        voteCount.moveTo(option);
        return voteCount;
    }

    private ArticleVoteCount lock(Long articleId) {
        return articleVoteCountRepository.findByIdForUpdate(articleId)
                .orElseThrow(() -> new IllegalStateException("집계 row 누락: articleId=" + articleId));
    }
}
