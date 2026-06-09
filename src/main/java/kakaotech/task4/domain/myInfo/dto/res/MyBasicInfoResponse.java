package kakaotech.task4.domain.myInfo.dto.res;

import kakaotech.task4.domain.user.entity.User;
import lombok.Builder;

@Builder
public record MyBasicInfoResponse(
        String email,
        String nickname,
        String profileImageUrl
) {
    public static MyBasicInfoResponse from(User user) {
        return MyBasicInfoResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
