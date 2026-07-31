package kakaotech.task4.articleVote;

import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.articleVote.entity.ArticleVote;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteCountRepository;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteRepository;
import kakaotech.task4.domain.articleVote.service.ArticleVoteService;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleVoteConcurrencyTest {

    @Autowired
    private ArticleVoteService articleVoteService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleVoteRepository articleVoteRepository;
    @Autowired
    private ArticleVoteCountRepository articleVoteCountRepository;

    @Test
    @DisplayName("같은 사용자의 선택 변경 요청이 동시에 실행되어도 집계에는 한 번만 반영된다.")
    void concurrentSameVoteChanges() throws Exception {
        // given
        Member member = memberRepository.saveAndFlush(createMember());
        Article article = articleRepository.saveAndFlush(createArticle(member));

        ArticleVoteCount voteCount = ArticleVoteCount.of(article.getArticleId());
        voteCount.increase(VoteOption.A);
        articleVoteCountRepository.saveAndFlush(voteCount);

        articleVoteRepository.saveAndFlush(
                ArticleVote.of(article, member, VoteOption.A)
        );

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Void> changeToB = () -> {
            ready.countDown();
            start.await();

            articleVoteService.vote(member, article, VoteOption.B);
            return null;
        };

        try {
            // when
            Future<Void> first = executorService.submit(changeToB);
            Future<Void> second = executorService.submit(changeToB);

            assertThat(ready.await(3, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);

            // then
            ArticleVote savedVote = articleVoteRepository
                    .findByArticleAndMember(article, member)
                    .orElseThrow();
            ArticleVoteCount savedCount = articleVoteCountRepository
                    .findById(article.getArticleId())
                    .orElseThrow();

            assertThat(savedVote.getVoteOption()).isEqualTo(VoteOption.B);
            assertThat(savedCount.getCountA()).isZero();
            assertThat(savedCount.getCountB()).isEqualTo(1);
        } finally {
            executorService.shutdownNow();
        }
    }

    private Member createMember() {
        return Member.builder()
                .memberUuid("member_uuid")
                .email("jade@example.com")
                .password("oldPassword123!")
                .nickname("jade")
                .profileImageUrl("https://example.com/profile.png")
                .build();
    }

    private Article createArticle(Member member) {
        CreateArticleRequest request = new CreateArticleRequest("제목", "A", "B");
        return Article.of("article_uuid", member, request);
    }
}
