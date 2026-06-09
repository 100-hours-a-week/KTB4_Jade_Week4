package kakaotech.task4.domain.myInfo.dto.req;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateMySecurityRequest(
        @Schema(description = "비밀번호", example = "JadeHello1234!")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,20}$",
                message = "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
        )
        String password,

        @Schema(description = "비밀번호 확인", example = "JadeHello1234!")
        @NotBlank(message = "비밀번호 확인을 입력해주세요.")
        String checkPassword
) {
    public boolean validatePasswordMatch() {
        return password.equals(checkPassword);
    }
}