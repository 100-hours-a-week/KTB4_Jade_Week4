package kakaotech.task4.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonFieldError implements FieldError {
    DUPLICATE_NICKNAME("nickname", "중복된 닉네임입니다."),
    INVALID_NICKNAME("nickname", "띄어쓰기 없이 10글자 이내로 입력해주세요."),
    PASSWORD_MISMATCH("checkNextPassword", "비밀번호가 다릅니다."),
    INVALID_NOW_PASSWORD("nowPassword", "현재 비밀번호가 일치하지 않습니다.");

    private final String field;
    private final String message;
}