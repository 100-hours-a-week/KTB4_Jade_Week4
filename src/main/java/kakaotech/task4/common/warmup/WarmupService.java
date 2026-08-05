package kakaotech.task4.common.warmup;

import kakaotech.task4.common.security.properties.CookieProperties;
import kakaotech.task4.common.security.token.AccessTokenProvider;
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

    public WarmupResult warmUp(Integer requestedCount) {
        int count = requestedCount == null ? warmupProperties.defaultCount() : requestedCount;
        int concurrency = warmupProperties.concurrency();

        String cookie = issueCookie();
        RestClient client = RestClient.builder()
                .baseUrl(warmupProperties.baseUrl())
                .build();

        long startedAt = System.currentTimeMillis();
        List<WarmupResult.TargetResult> results = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        try {
            for (String path : resolveTargets()) {
                results.add(warmUpTarget(executor, client, path, count, cookie));
            }
        } finally {
            executor.shutdown();
        }

        WarmupResult result = new WarmupResult(
                count,
                concurrency,
                System.currentTimeMillis() - startedAt,
                cookie != null,
                results);

        log.info("워밍업 완료: {}", result);
        return result;
    }

    private WarmupResult.TargetResult warmUpTarget(ExecutorService executor,
                                                   RestClient client,
                                                   String path,
                                                   int count,
                                                   String cookie) {
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();
        long startedAt = System.currentTimeMillis();

        List<Future<?>> futures = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(() -> {
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
            }));
        }

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

        return new WarmupResult.TargetResult(
                path,
                success.get(),
                failure.get(),
                System.currentTimeMillis() - startedAt);
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
