package kakaotech.task4.domain.auth.code;

import kakaotech.task4.common.exception.FieldError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthFieldError implements FieldError {
    DUPLICATE_EMAIL("email", "중복된 이메일입니다.");

    private final String field;
    private final String message;
}