package kakaotech.task4.common.exception;

import jakarta.validation.Path;
import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(final CustomException e) {
        log.warn("[CustomException] {}", e.getMessage());
        return toErrorResponse(e.getExceptionCode(), e.getFields());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        Map<String, Object> fields = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        ExceptionCode error = resolveValidationErrorCode(e);
        log.warn("[Validation] code={}, fields={}", error.getCode(), fields);
        return toErrorResponse(error, fields);
    }

    private ExceptionCode resolveValidationErrorCode(MethodArgumentNotValidException e) {
        boolean hasMissing = e.getBindingResult().getFieldErrors().stream()
                .anyMatch(error -> "NotBlank".equals(error.getCode()));
        return hasMissing ? GlobalExceptionCode.BAD_REQUEST : GlobalExceptionCode.VALIDATION_ERROR;
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolation(final jakarta.validation.ConstraintViolationException e) {
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(
            final DataIntegrityViolationException e
    ) {
        log.warn("[DataIntegrityViolationException] {}", e.getMessage());

        ExceptionCode error = GlobalExceptionCode.DATA_INTEGRITY_VIOLATION;
        return toErrorResponse(error);
    }

    private String extractFieldName(Path propertyPath) {
        String field = null;
        for (Path.Node node : propertyPath) {
            field = node.getName();
        }
        return field;
    }


    private ResponseEntity<ApiResponse<Void>> toErrorResponse(ExceptionCode error) {
        ApiResponse<Void> body = ApiResponse.error(error);
        HttpStatus status = error.getStatus();
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ApiResponse<Void>> toErrorResponse(ExceptionCode error, Map<String, Object> fields) {
        ApiResponse<Void> body = ApiResponse.error(error, fields);
        HttpStatus status = error.getStatus();
        return ResponseEntity.status(status).body(body);
    }

}