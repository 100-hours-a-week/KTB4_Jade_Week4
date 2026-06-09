package kakaotech.task4.domain.myInfo.dto.res;

import kakaotech.task4.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UpdateMyBasicInfoResponse(
        String profileImageUrl
) {
    public static UpdateMyBasicInfoResponse from(User user) {
        return UpdateMyBasicInfoResponse.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}