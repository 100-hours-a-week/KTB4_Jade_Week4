package kakaotech.task4.domain.myInfo.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record UpdateMyBasicInfoRequest(
        @Schema(description = "닉네임", example = "jade2")
        @Pattern(regexp = "^\\S{1,10}$", message = "띄어쓰기 없이 10글자 이내로 입력해주세요.")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "profileImageUrl")
        String profileImageUrl
) {}