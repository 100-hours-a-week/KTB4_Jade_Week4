package kakaotech.task4.domain.myInfo.dto.res;

import kakaotech.task4.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MyBasicInfoResponse(
        String email,
        String nickname,
        String profileImageUrl
) {
    public static MyBasicInfoResponse from(Member member) {
        return MyBasicInfoResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
