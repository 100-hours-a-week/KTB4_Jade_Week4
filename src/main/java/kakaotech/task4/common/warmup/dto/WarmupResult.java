package kakaotech.task4.common.warmup.dto;

import java.util.List;

public record WarmupResult(
        int requestedCount,
        int concurrency,
        long elapsedMs,
        boolean authenticated,
        List<TargetResult> targets
) {
    public record TargetResult(
            String path,
            int success,
            int failure,
            long elapsedMs
    ) {
    }
}
