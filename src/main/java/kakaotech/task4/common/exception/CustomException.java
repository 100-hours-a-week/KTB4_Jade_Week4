package kakaotech.task4.common.exception;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
