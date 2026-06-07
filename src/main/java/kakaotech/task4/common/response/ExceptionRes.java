package kakaotech.task4.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExceptionRes(
        String code,
        HttpStatus status,
        String message,
        Map<String, Object> fields
) {
    public static ExceptionRes from(ExceptionCode error){
        return ExceptionRes.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    public static ExceptionRes from(ExceptionCode error, Map<String, Object> fields) {
        return ExceptionRes.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(error.getMessage())
                .fields(fields)
                .build();
    }
}
