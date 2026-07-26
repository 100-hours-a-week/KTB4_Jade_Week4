package kakaotech.task4.articleVote;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleVote.dto.res.ArticleVoteResponse;
import kakaotech.task4.domain.articleVote.entity.ArticleVote;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteCountRepository;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteRepository;
import kakaotech.task4.domain.articleVote.service.ArticleVoteService;
import kakaotech.task4.domain.articleVote.service.count.VoteCountUpdater;
import kakaotech.task4.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleVoteServiceTest {

    private static final Long ARTICLE_ID = 1L;

    @Mock
    private ArticleVoteRepository articleVoteRepository;

    @Mock
    private ArticleVoteCountRepository articleVoteCountRepository;

    @Mock
    private VoteCountUpdater voteCountUpdater;

    @InjectMocks
    private ArticleVoteService articleVoteService;

    @Test
    @DisplayName("투표 이력이 없으면 이력을 저장하고 선택한 선택지의 카운트를 증가시킨다")
    void voteFirstTime() {
        // given
        Article article = createArticle();
        Member member = createMember();
        ArticleVoteCount voteCount = ArticleVoteCount.of(ARTICLE_ID);
        voteCount.increase(VoteOption.A);

        when(articleVoteRepository.findByArticleAndMember(article, member)).thenReturn(Optional.empty());
        when(voteCountUpdater.increase(ARTICLE_ID, VoteOption.A)).thenReturn(voteCount);

        // when
        ArticleVoteResponse response = articleVoteService.vote(member, article, VoteOption.A);

        // then
        verify(articleVoteRepository).save(any(ArticleVote.class));
        verify(voteCountUpdater).increase(ARTICLE_ID, VoteOption.A);
        assertThat(response.voteCountA()).isEqualTo(1);
        assertThat(response.myVote()).isEqualTo(VoteOption.A);
        assertThat(response.changed()).isTrue();
        assertThat(response.wasFirst()).isTrue();
    }

    @Test
    @DisplayName("이미 선택한 선택지를 다시 요청하면 카운트를 바꾸지 않고 changed=false 로 응답한다")
    void voteSameOption() {
        // given
        Article article = createArticle();
        Member member = createMember();
        ArticleVote articleVote = ArticleVote.of(article, member, VoteOption.A);

        when(articleVoteRepository.findByArticleAndMember(article, member)).thenReturn(Optional.of(articleVote));
        when(articleVoteCountRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(ArticleVoteCount.of(ARTICLE_ID)));

        // when
        ArticleVoteResponse response = articleVoteService.vote(member, article, VoteOption.A);

        // then
        verify(articleVoteRepository, never()).save(any(ArticleVote.class));
        verifyNoInteractions(voteCountUpdater);
        assertThat(articleVote.getVoteOption()).isEqualTo(VoteOption.A);
        assertThat(response.changed()).isFalse();
        assertThat(response.wasFirst()).isFalse();
    }

    @Test
    @DisplayName("다른 선택지로 투표하면 이력의 선택지를 바꾸고 카운트를 이동시킨다")
    void voteChangeOption() {
        // given
        Article article = createArticle();
        Member member = createMember();
        ArticleVote articleVote = ArticleVote.of(article, member, VoteOption.A);
        ArticleVoteCount voteCount = ArticleVoteCount.of(ARTICLE_ID);
        voteCount.increase(VoteOption.A);
        voteCount.moveTo(VoteOption.B);

        when(articleVoteRepository.findByArticleAndMember(article, member)).thenReturn(Optional.of(articleVote));
        when(voteCountUpdater.moveTo(ARTICLE_ID, VoteOption.B)).thenReturn(voteCount);

        // when
        ArticleVoteResponse response = articleVoteService.vote(member, article, VoteOption.B);

        // then
        verify(articleVoteRepository, never()).save(any(ArticleVote.class));
        verify(voteCountUpdater).moveTo(ARTICLE_ID, VoteOption.B);
        assertThat(articleVote.getVoteOption()).isEqualTo(VoteOption.B);
        assertThat(response.voteCountA()).isZero();
        assertThat(response.voteCountB()).isEqualTo(1);
        assertThat(response.myVote()).isEqualTo(VoteOption.B);
        assertThat(response.changed()).isTrue();
        assertThat(response.wasFirst()).isFalse();
    }

    @Test
    @DisplayName("집계 row 가 없는 게시글은 0표로 응답한다")
    void voteCountNotFound() {
        // given
        Article article = createArticle();

        when(articleVoteCountRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        // when
        ArticleVoteCount voteCount = articleVoteService.findVoteCount(article);

        // then
        assertThat(voteCount.getCountA()).isZero();
        assertThat(voteCount.getCountB()).isZero();
        verifyNoInteractions(articleVoteRepository);
    }

    private Article createArticle() {
        Article article = mock(Article.class);
        when(article.getArticleId()).thenReturn(ARTICLE_ID);
        return article;
    }

    private Member createMember() {
        return Member.builder()
                .memberUuid("member_uuid")
                .email("jade@example.com")
                .password("encoded")
                .nickname("jade")
                .profileImageUrl("https://example.com/profile.png")
                .build();
    }
}
