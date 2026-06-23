package kakaotech.task4.domain.myInfo.dto.res;

import kakaotech.task4.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record UpdateMyBasicInfoResponse(
        String profileImageUrl
) {
    public static UpdateMyBasicInfoResponse from(Member member) {
        return UpdateMyBasicInfoResponse.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}