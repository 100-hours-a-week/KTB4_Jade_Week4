package kakaotech.task4.domain.auth.dto.res;

import lombok.Builder;

@Builder
public record SignInResponse (
        String profileImageUrl
) {
    public static SignInResponse from(String profileImageUrl) {
        return SignInResponse.builder()
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
