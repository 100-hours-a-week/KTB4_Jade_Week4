package kakaotech.task4.common.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.response.ExceptionRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //todo : 삼항 연산자로 필드 있고 없고 분리 중. 더 좋은 방법 없나?
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(final CustomException e) {
        log.warn("[CustomException] {}", e.getMessage());
        ExceptionCode error = e.getExceptionCode();
        Map<String, Object> fields = e.getFields();

        return fields.isEmpty()
                ? toErrorResponse(error)
                : toErrorResponse(error, fields);
    }

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

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolation(final ConstraintViolationException e) {
        Map<String, Object> fields = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String field = extractFieldName(violation.getPropertyPath());
            fields.put(field, violation.getMessage());
        });

        log.warn("[ConstraintViolation] fields={}", fields);
        ExceptionCode error = GlobalExceptionCode.VALIDATION_ERROR;
        return toErrorResponse(error, fields);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<?> handleNoHandlerFound(final NoHandlerFoundException e) {
        log.warn("[NoHandlerFound] {}", e.getMessage());
        ExceptionCode error = GlobalExceptionCode.NOT_FOUND;
        return toErrorResponse(error);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleServerException(final Exception e) {
        log.error("[Exception] {}", e.getMessage());
        ExceptionCode error = GlobalExceptionCode.INTERNAL_SERVER_ERROR;
        return toErrorResponse(error);
    }

    private String extractFieldName(Path propertyPath) {
        String field = null;
        for (Path.Node node : propertyPath) {
            field = node.getName();
        }
        return field;
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