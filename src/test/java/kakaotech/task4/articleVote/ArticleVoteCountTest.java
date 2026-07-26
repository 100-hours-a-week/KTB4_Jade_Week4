package kakaotech.task4.articleVote;

import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleVoteCountTest {

    private static final Long ARTICLE_ID = 1L;

    @Test
    @DisplayName("increase 는 선택한 선택지의 카운트만 1 올린다")
    void increase() {
        // given
        ArticleVoteCount voteCount = ArticleVoteCount.of(ARTICLE_ID);

        // when
        voteCount.increase(VoteOption.A);
        voteCount.increase(VoteOption.A);
        voteCount.increase(VoteOption.B);

        // then
        assertThat(voteCount.getCountA()).isEqualTo(2);
        assertThat(voteCount.getCountB()).isEqualTo(1);
    }

    @Test
    @DisplayName("moveTo 는 기존 선택지에서 한 표를 빼고 새 선택지에 더한다")
    void moveTo() {
        // given
        ArticleVoteCount voteCount = ArticleVoteCount.of(ARTICLE_ID);
        voteCount.increase(VoteOption.A);
        voteCount.increase(VoteOption.A);

        // when
        voteCount.moveTo(VoteOption.B);

        // then
        assertThat(voteCount.getCountA()).isEqualTo(1);
        assertThat(voteCount.getCountB()).isEqualTo(1);
    }

    @Test
    @DisplayName("빼야 할 카운트가 0이면 음수로 내려가지 않는다")
    void moveToDoesNotGoNegative() {
        // given
        ArticleVoteCount voteCount = ArticleVoteCount.of(ARTICLE_ID);

        // when
        voteCount.moveTo(VoteOption.B);

        // then
        assertThat(voteCount.getCountA()).isZero();
        assertThat(voteCount.getCountB()).isEqualTo(1);
    }
}
