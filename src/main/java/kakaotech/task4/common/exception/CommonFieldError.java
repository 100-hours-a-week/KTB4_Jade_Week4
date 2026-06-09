package kakaotech.task4.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonFieldError implements FieldError {
    DUPLICATE_NICKNAME("nickname", "중복된 닉네임입니다."),
    INVALID_NICKNAME("nickname", "띄어쓰기 없이 10글자 이내로 입력해주세요."),
    PASSWORD_MISMATCH("checkPassword", "비밀번호가 다릅니다."),
    INVALID_PASSWORD("password", "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.");

    private final String field;
    private final String message;
}