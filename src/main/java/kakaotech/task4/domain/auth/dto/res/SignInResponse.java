package kakaotech.task4.domain.auth.dto.res;

import lombok.Builder;

@Builder
public record SignInResponse (
        String profileImageUrl,
        String userUuid
) {
    public static SignInResponse from(String profileImageUrl, String userUuid) {
        return SignInResponse.builder()
                .profileImageUrl(profileImageUrl)
                .userUuid(userUuid)
                .build();
    }
}