package kakaotech.task4.common.exception.ExceptionCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalExceptionCode implements ExceptionCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GLOBAL-400-001", "필수 값이 제외되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL-404-001", "존재하지 않는 페이지입니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "COMMON_409", "데이터 정합성 제약을 위반했습니다."),
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "GLOBAL-422-001", "필드의 유효성 검사가 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL-500-001", "서버 에러입니다. 서버 팀에 문의해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
