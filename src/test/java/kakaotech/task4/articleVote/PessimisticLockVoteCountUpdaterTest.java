package kakaotech.task4.articleVote;

import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteCountRepository;
import kakaotech.task4.domain.articleVote.service.count.PessimisticLockVoteCountUpdater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PessimisticLockVoteCountUpdaterTest {

    private static final Long ARTICLE_ID = 1L;

    @Mock
    private ArticleVoteCountRepository articleVoteCountRepository;

    @InjectMocks
    private PessimisticLockVoteCountUpdater voteCountUpdater;

    @Test
    @DisplayName("increase 는 집계 row 를 락으로 조회한 뒤 카운트를 올린다")
    void increaseLocksRow() {
        // given
        ArticleVoteCount voteCount = ArticleVoteCount.of(ARTICLE_ID);
        when(articleVoteCountRepository.findByIdForUpdate(ARTICLE_ID)).thenReturn(Optional.of(voteCount));

        // when
        ArticleVoteCount result = voteCountUpdater.increase(ARTICLE_ID, VoteOption.A);

        // then
        verify(articleVoteCountRepository).findByIdForUpdate(ARTICLE_ID);
        verify(articleVoteCountRepository, never()).findById(ARTICLE_ID);
        assertThat(result.getCountA()).isEqualTo(1);
    }

    @Test
    @DisplayName("moveTo 는 집계 row 를 락으로 조회한 뒤 표를 옮긴다")
    void moveToLocksRow() {
        // given
        ArticleVoteCount voteCount = ArticleVoteCount.of(ARTICLE_ID);
        voteCount.increase(VoteOption.A);
        when(articleVoteCountRepository.findByIdForUpdate(ARTICLE_ID)).thenReturn(Optional.of(voteCount));

        // when
        ArticleVoteCount result = voteCountUpdater.moveTo(ARTICLE_ID, VoteOption.B);

        // then
        verify(articleVoteCountRepository).findByIdForUpdate(ARTICLE_ID);
        assertThat(result.getCountA()).isZero();
        assertThat(result.getCountB()).isEqualTo(1);
    }

    @Test
    @DisplayName("집계 row 가 없으면 생성하지 않고 즉시 실패한다")
    void failsWhenRowMissing() {
        // given
        when(articleVoteCountRepository.findByIdForUpdate(ARTICLE_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> voteCountUpdater.increase(ARTICLE_ID, VoteOption.B))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.valueOf(ARTICLE_ID));

        verify(articleVoteCountRepository, never()).save(any(ArticleVoteCount.class));
    }
}
