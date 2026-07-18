package kakaotech.task4.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        String code,
        Map<String, Object> fields
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, data, message, null, null);
    }

    public static ApiResponse<Void> error(ExceptionCode exceptionCode) {
        return new ApiResponse<>(false, null, exceptionCode.getMessage(), exceptionCode.getCode(), null);
    }

    public static ApiResponse<Void> error(ExceptionCode exceptionCode, Map<String, Object> fields) {
        return new ApiResponse<>(false, null, exceptionCode.getMessage(), exceptionCode.getCode(), fields);
    }
}
