package kakaotech.task4.domain.auth.dto.res;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SignInResponse(
        String profileImageUrl,
        Instant accessTokenExpiresAt
) {
    public static SignInResponse from(String profileImageUrl, Instant accessTokenExpiresAt) {
        return SignInResponse.builder()
                .profileImageUrl(profileImageUrl)
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .build();
    }
}