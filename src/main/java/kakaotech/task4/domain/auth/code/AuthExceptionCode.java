package kakaotech.task4.domain.auth.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {
    CONFLICT(HttpStatus.CONFLICT, "AUTH-409-001", "중복된 데이터입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "AUTH-409-001", "중복된 이메일입니다."),  //사실 code 안쓰임
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "AUTH-409-001", "중복된 닉네임입니다."),  //사실 code 안쓰임
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "AUTH-422-001", "필드의 유효성 검사가 올바르지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.UNPROCESSABLE_ENTITY, "AUTH-422-001", "비밀번호가 다릅니다."), //사실 code 안쓰임
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-401-001", "이메일 또는 비밀번호가 다릅니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-401-002", "로그인 후 사용할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

//todo : code 안쓰는 enum -> 필드매핑 메시지 -> 클래스 따로 분리해야 할 듯