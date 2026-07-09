package kakaotech.task4.common.security.jwt.code;
import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtExceptionCode implements ExceptionCode {
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT-401-001", "Access Token이 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT-401-002", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-401-003", "Access Token이 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}