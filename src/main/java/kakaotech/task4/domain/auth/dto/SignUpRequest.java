package kakaotech.task4.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 주소 형식을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,20}$",
                message = "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String checkPassword,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(
                regexp = "^\\S{1,10}$",
                message = "띄어쓰기 없이 10글자 이내로 입력해주세요."
        )
        String nickname,

        @NotBlank(message = "프로필 사진을 추가해주세요.")
        String profileImageUrl
) {
        public boolean validatePasswordMatch() {
                return password.equals(checkPassword);
        }

}
