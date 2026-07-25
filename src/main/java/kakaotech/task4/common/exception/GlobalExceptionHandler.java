package kakaotech.task4.common.exception;

import jakarta.validation.Path;
import kakaotech.task4.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MISSING_VALUE_CODE = "NotBlank";

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(final CustomException e) {
        log.warn("[CustomException] {}", e.getMessage());
        return toErrorResponse(e.getExceptionCode(), e.getFields());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        Map<String, Object> fields = toFieldMessages(e.getBindingResult().getFieldErrors());

        ExceptionCode error = resolveValidationErrorCode(e);
        log.warn("[Validation] code={}, fields={}", error.getCode(), fields);
        return toErrorResponse(error, fields);
    }

    private Map<String, Object> toFieldMessages(List<org.springframework.validation.FieldError> fieldErrors) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fieldErrors.stream()
                .filter(error -> MISSING_VALUE_CODE.equals(error.getCode()))
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        fieldErrors.forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return fields;
    }

    private ExceptionCode resolveValidationErrorCode(MethodArgumentNotValidException e) {
        boolean hasMissing = e.getBindingResult().getFieldErrors().stream()
                .anyMatch(error -> MISSING_VALUE_CODE.equals(error.getCode()));
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<?> handleMessageNotReadable(final HttpMessageNotReadableException e) {
        log.warn("[MessageNotReadable] {}", e.getMessage());
        ExceptionCode error = GlobalExceptionCode.MALFORMED_REQUEST;
        return toErrorResponse(error);
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