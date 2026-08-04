package kakaotech.task4.common.warmup.dto;

import java.util.List;

public record WarmupResult(
        int requestedCount,
        int concurrency,
        long elapsedMs,
        boolean authenticated,
        List<TargetResult> targets
) {
    /**
     * authenticated가 false면 인증이 필요한 경로가 401로 끝났다는 뜻이다.
     * 회원이 하나도 없는 상태(첫 배포 등)에서 발생하며, JPA 조회 경로는 데워지지 않는다.
     */
    public record TargetResult(
            String path,
            int success,
            int failure,
            long elapsedMs
    ) {
    }
}
