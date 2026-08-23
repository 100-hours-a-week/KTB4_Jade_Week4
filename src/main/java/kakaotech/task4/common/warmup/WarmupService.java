package kakaotech.task4.common.warmup;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.properties.CookieProperties;
import kakaotech.task4.common.security.token.AccessTokenProvider;
import kakaotech.task4.common.warmup.code.WarmupExceptionCode;
import kakaotech.task4.common.warmup.dto.WarmupResult;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@Service
@RequiredArgsConstructor
public class WarmupService {
    static final String ARTICLE_UUID_PLACEHOLDER = "{articleUuid}";

    private final WarmupProperties warmupProperties;
    private final CookieProperties cookieProperties;
    private final AccessTokenProvider accessTokenProvider;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    public WarmupResult warmUp(String secret, Integer requestedCount) {
        validateSecret(secret);

        int count = requestedCount == null ? warmupProperties.defaultCount() : requestedCount;
        int concurrency = warmupProperties.concurrency();

        String cookie = issueCookie();
        long startedAt = System.currentTimeMillis();
        List<WarmupResult.TargetResult> results = warmUpTargets(count, concurrency, cookie);
        WarmupResult result = createResult(count, concurrency, cookie, startedAt, results);

        log.info("워밍업 완료: {}", result);
        return result;
    }

    private List<WarmupResult.TargetResult> warmUpTargets(int count, int concurrency, String cookie) {
        RestClient client = RestClient.builder()
                .baseUrl(warmupProperties.baseUrl())
                .build();
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        List<WarmupResult.TargetResult> results = new ArrayList<>();
        try {
            for (String path : resolveTargets()) {
                results.add(warmUpTarget(executor, client, path, count, cookie));
            }
            return results;
        } finally {
            executor.shutdown();
        }
    }

    private WarmupResult createResult(int count,
                                      int concurrency,
                                      String cookie,
                                      long startedAt,
                                      List<WarmupResult.TargetResult> results) {
        return new WarmupResult(
                count,
                concurrency,
                System.currentTimeMillis() - startedAt,
                cookie != null,
                results);
    }

    private void validateSecret(String secret) {
        if (!warmupProperties.enabled() || secret == null || !MessageDigest.isEqual(
                secret.getBytes(StandardCharsets.UTF_8),
                warmupProperties.secret().getBytes(StandardCharsets.UTF_8))) {
            throw new CustomException(WarmupExceptionCode.NOT_FOUND);
        }
    }

    private WarmupResult.TargetResult warmUpTarget(ExecutorService executor,
                                                   RestClient client,
                                                   String path,
                                                   int count,
                                                   String cookie) {
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();
        long startedAt = System.currentTimeMillis();

        List<Future<?>> futures = submitWarmupTasks(executor, client, path, count, cookie, success, failure);
        awaitCompletion(futures, failure);

        return new WarmupResult.TargetResult(
                path,
                success.get(),
                failure.get(),
                System.currentTimeMillis() - startedAt);
    }

    private List<Future<?>> submitWarmupTasks(ExecutorService executor,
                                              RestClient client,
                                              String path,
                                              int count,
                                              String cookie,
                                              AtomicInteger success,
                                              AtomicInteger failure) {
        List<Future<?>> futures = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(() -> executeWarmupRequest(client, path, cookie, success, failure)));
        }
        return futures;
    }

    private void executeWarmupRequest(RestClient client,
                                      String path,
                                      String cookie,
                                      AtomicInteger success,
                                      AtomicInteger failure) {
        try {
            RestClient.RequestHeadersSpec<?> request = client.get().uri(path);
            if (cookie != null) {
                request = request.header("Cookie", cookie);
            }
            request.retrieve().toBodilessEntity();
            success.incrementAndGet();
        } catch (Exception e) {
            failure.incrementAndGet();
        }
    }

    private void awaitCompletion(List<Future<?>> futures, AtomicInteger failure) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                failure.incrementAndGet();
            }
        }
    }

    private List<String> resolveTargets() {
        List<String> targets = warmupProperties.targets();
        if (targets.stream().noneMatch(path -> path.contains(ARTICLE_UUID_PLACEHOLDER))) {
            return targets;
        }

        String articleUuid = articleRepository.findFirstPage(PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(Article::getArticleUuid)
                .orElse(null);

        List<String> resolved = new ArrayList<>(targets.size());
        for (String path : targets) {
            if (!path.contains(ARTICLE_UUID_PLACEHOLDER)) {
                resolved.add(path);
            } else if (articleUuid != null) {
                resolved.add(path.replace(ARTICLE_UUID_PLACEHOLDER, articleUuid));
            } else {
                log.warn("워밍업: 게시글이 없어 {} 경로를 건너뛴다.", path);
            }
        }
        return resolved;
    }

    private String issueCookie() {
        List<Member> members = memberRepository.findAll(PageRequest.of(0, 1)).getContent();
        if (members.isEmpty()) {
            log.warn("워밍업: 회원이 없어 인증 없이 진행한다. 인증이 필요한 경로는 데워지지 않는다.");
            return null;
        }

        String token = accessTokenProvider
                .createAccessToken(members.getFirst().getMemberUuid())
                .token();

        return cookieProperties.accessName() + "=" + token;
    }
}
