package kakaotech.task4.articleLike;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.articleLike.code.ArticleLikeExceptionCode;
import kakaotech.task4.domain.articleLike.repository.ArticleLikeRepository;
import kakaotech.task4.domain.articleLike.service.ArticleLikeService;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleLikeConcurrencyTest {

    @Autowired private ArticleLikeService articleLikeService;
    @Autowired private ArticleLikeRepository articleLikeRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private MemberRepository memberRepository;

    private Member member;
    private Article article;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(
                Member.builder()
                        .email("concurrent@example.com")
                        .password("encoded_pw")
                        .nickname("tester")
                        .profileImageUrl("https://example.com/profile.png")
                        .memberUuid(UUID.randomUUID().toString())
                        .build()
        );

        article = articleRepository.save(
                Article.builder()
                        .title("title")
                        .content("본문")
                        .member(member)
                        .articleUuid(UUID.randomUUID().toString())
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        articleLikeRepository.deleteAll();
        articleRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("같은 유저가 동시에 좋아요를 여러 번 눌러도 한 번만 저장되고, 나머지는 ALREADY_LIKED로 처리된다")
    void concurrentLikeShouldSaveOnlyOnce() throws InterruptedException {
        // given
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger alreadyLikedCount = new AtomicInteger();
        AtomicInteger unexpectedCount = new AtomicInteger();

        // when: 20개 스레드가 동시에 좋아요 요청
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startSignal.await();
                    articleLikeService.like(member, article);
                    successCount.incrementAndGet();
                } catch (CustomException e) {
                    if (e.getExceptionCode() == ArticleLikeExceptionCode.ALREADY_LIKED) {
                        alreadyLikedCount.incrementAndGet();
                    } else {
                        unexpectedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println(">>> 예상치 못한 예외 = " + e.getClass().getSimpleName());
                    unexpectedCount.incrementAndGet();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown();  // 전원 동시 출발
        doneSignal.await();
        executor.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(alreadyLikedCount.get()).isEqualTo(threadCount - 1);
        assertThat(unexpectedCount.get())
                .as("DataIntegrityViolationException이 409로 변환되지 않고 새어나갔다")
                .isZero();

        assertThat(articleLikeRepository.count()).isEqualTo(1);

        Article found = articleRepository.findById(article.getArticleId()).orElseThrow();
        assertThat(found.getLikedCount()).isEqualTo(1);  // 카운터도 딱 1
    }
}