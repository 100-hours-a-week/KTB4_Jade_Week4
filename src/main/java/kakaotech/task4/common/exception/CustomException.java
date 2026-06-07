package kakaotech.task4.common.exception;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ExceptionCode exceptionCode;
    private final Map<String, Object> fields;

    public CustomException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.fields = Collections.emptyMap();
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
