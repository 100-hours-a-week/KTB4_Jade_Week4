package kakaotech.task4.domain.file.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.file.code.FileExceptionCode;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UploadUrlRateLimiter {
    private static final int MAX_ISSUE_PER_WINDOW = 10;
    private static final Duration WINDOW = Duration.ofHours(1);
    private static final String FORWARDED_FOR = "X-Forwarded-For";

    private final Cache<String, AtomicInteger> issueCounts = Caffeine.newBuilder()
            .expireAfterWrite(WINDOW)
            .maximumSize(10_000)
            .build();

    public void check(HttpServletRequest request) {
        AtomicInteger count = issueCounts.get(clientIp(request), key -> new AtomicInteger());

        if (count.incrementAndGet() > MAX_ISSUE_PER_WINDOW) {
            throw new CustomException(FileExceptionCode.TOO_MANY_UPLOAD_REQUESTS);
        }
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(FORWARDED_FOR);

        if (forwardedFor == null || forwardedFor.isBlank()) {
            return request.getRemoteAddr();
        }
        return forwardedFor.split(",")[0].trim();
    }
}
