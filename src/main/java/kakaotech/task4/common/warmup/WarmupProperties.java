package kakaotech.task4.common.warmup;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "warmup")
public record WarmupProperties(
        String secret,
        Integer defaultCount,
        Integer concurrency,
        String baseUrl,
        List<String> targets
) {
    private static final int DEFAULT_COUNT = 1000;
    private static final int DEFAULT_CONCURRENCY = 10;
    private static final String DEFAULT_BASE_URL = "http://127.0.0.1:8080/api";
    private static final List<String> DEFAULT_TARGETS = List.of("/articles?size=10");

    public WarmupProperties {
        defaultCount = defaultCount == null ? DEFAULT_COUNT : defaultCount;
        concurrency = concurrency == null ? DEFAULT_CONCURRENCY : concurrency;
        baseUrl = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_BASE_URL : baseUrl;
        targets = (targets == null || targets.isEmpty()) ? DEFAULT_TARGETS : targets;
    }

    public boolean enabled() {
        return secret != null && !secret.isBlank();
    }
}
