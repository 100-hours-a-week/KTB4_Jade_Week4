package kakaotech.task4.common.exception.ExceptionCode;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {
    String getCode();
    HttpStatus getStatus();
    String getMessage();
}
