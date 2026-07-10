package kakaotech.task4.common.security.token.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record RefreshTokenInfo(
        String memberUuid,
        Instant expiresAt
) {
    public static RefreshTokenInfo of(String memberUuid, Instant expiresAt) {
        return RefreshTokenInfo.builder()
                .memberUuid(memberUuid)
                .expiresAt(expiresAt)
                .build();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}