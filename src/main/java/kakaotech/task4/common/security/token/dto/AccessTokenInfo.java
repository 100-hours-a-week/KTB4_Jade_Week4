package kakaotech.task4.common.security.token.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AccessTokenInfo(
        String token,
        Instant expiresAt
) {
    public static AccessTokenInfo of(String token, Instant expiresAt) {
        return AccessTokenInfo.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }
}