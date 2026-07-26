package kakaotech.task4.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kakaotech.task4.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        HttpStatus status,
        boolean success,
        T data,
        String message,
        String code,
        Map<String, Object> fields
) {
    public static <T> ApiResponse<T> success(T data) {
        return of(HttpStatus.OK, null, data);
    }

    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return of(successCode.getStatus(), successCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(HttpStatus.CREATED, null, data);
    }

    public ResponseEntity<ApiResponse<T>> toEntity() {
        return ResponseEntity.status(status).body(this);
    }

    public static ApiResponse<Void> error(ExceptionCode exceptionCode) {
        return error(exceptionCode, null);
    }

    public static ApiResponse<Void> error(ExceptionCode exceptionCode, Map<String, Object> fields) {
        Map<String, Object> normalizedFields = (fields == null || fields.isEmpty()) ? null : fields;
        return new ApiResponse<>(
                exceptionCode.getStatus(),
                false,
                null,
                exceptionCode.getMessage(),
                exceptionCode.getCode(),
                normalizedFields
        );
    }

    private static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, true, data, message, null, null);
    }

}
