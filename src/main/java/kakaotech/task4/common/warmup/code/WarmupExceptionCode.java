package kakaotech.task4.common.warmup.code;

import kakaotech.task4.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WarmupExceptionCode implements ExceptionCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "WARMUP-404-001", "존재하지 않는 페이지입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
