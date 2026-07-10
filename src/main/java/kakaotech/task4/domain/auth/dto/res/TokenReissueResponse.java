package kakaotech.task4.domain.auth.dto.res;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TokenReissueResponse(
        Instant accessTokenExpiresAt
) {
    public static TokenReissueResponse from(Instant accessTokenExpiresAt) {
        return TokenReissueResponse.builder()
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .build();
    }
}