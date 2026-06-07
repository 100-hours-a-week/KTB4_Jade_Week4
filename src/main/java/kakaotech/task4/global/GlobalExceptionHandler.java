package kakaotech.task4.global;

import kakaotech.task4.global.ExceptionCode.ExceptionCode;
import kakaotech.task4.global.ExceptionCode.GlobalExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //todo : 삼항 연산자로 400과 422분리 중. 더 좋은 방법 없을까?
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        boolean hasMissing = e.getBindingResult().getFieldErrors().stream()
                .anyMatch(error -> "NotBlank".equals(error.getCode()));

        Map<String, Object> fields = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        log.warn("[Validation] hasMissing={}, fields={}", hasMissing, fields);
        ExceptionCode error = hasMissing ? GlobalExceptionCode.BAD_REQUEST : GlobalExceptionCode.VALIDATION_ERROR;
        return toErrorResponse(error, fields);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleServerException(final Exception e) {
        log.error("[Exception] {}", e.getMessage());
        ExceptionCode error = GlobalExceptionCode.INTERNAL_SERVER_ERROR;
        return toErrorResponse(error);
    }

    private ResponseEntity<ExceptionRes> toErrorResponse(ExceptionCode error) {
        ExceptionRes body = ExceptionRes.from(error);
        HttpStatus status = error.getStatus();
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ExceptionRes> toErrorResponse(ExceptionCode error, Map<String, Object> fields) {
        ExceptionRes body = ExceptionRes.from(error, fields);
        HttpStatus status = error.getStatus();
        return ResponseEntity.status(status).body(body);
    }

}