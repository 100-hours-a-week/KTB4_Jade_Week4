package kakaotech.task4.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @Schema(description = "이메일", example = "test@naver.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 주소 형식을 입력해주세요.")
        String email,

        @Schema(description = "비밀번호", example = "Password123!")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,20}$",
                message = "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
        )
        String password,

        @Schema(description = "비밀번호 확인", example = "Password123!")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String checkPassword,

        @Schema(description = "닉네임", example = "jade")
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(
                regexp = "^\\S{1,10}$",
                message = "띄어쓰기 없이 10글자 이내로 입력해주세요."
        )
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
        @NotBlank(message = "프로필 사진을 추가해주세요.")
        String profileImageUrl
) {
        public boolean validatePasswordMatch() {
                return password.equals(checkPassword);
        }
}
