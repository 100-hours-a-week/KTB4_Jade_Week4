package kakaotech.task4.common.exception.ExceptionCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalExceptionCode implements ExceptionCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL-500-001", "서버 에러입니다. 서버 팀에 문의해주세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GLOBAL-400-001", "필수 값이 제외되었습니다."),
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "GLOBAL-422-001", "필드의 유효성 검사가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
