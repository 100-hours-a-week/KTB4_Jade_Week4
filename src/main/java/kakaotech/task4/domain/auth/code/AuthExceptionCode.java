package kakaotech.task4.domain.auth.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {
    MISSING_AUTH_HEADER(HttpStatus.BAD_REQUEST, "AUTH-400-001", "Authorization 헤더가 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-401-001", "인증되지 않은 사용자입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-401-002", "이메일 또는 비밀번호가 다릅니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-403-001", "접근 권한이 없습니다."),
    INVALID_CSRF_TOKEN(HttpStatus.FORBIDDEN, "AUTH-403-002", "CSRF Token이 유효하지 않습니다."),
    CONFLICT(HttpStatus.CONFLICT, "AUTH-409-001", "중복된 데이터입니다."),
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "AUTH-422-001", "필드의 유효성 검사가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}