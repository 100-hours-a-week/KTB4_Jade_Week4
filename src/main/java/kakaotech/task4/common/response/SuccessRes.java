package kakaotech.task4.common.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record SuccessRes (
        HttpStatus status,
        String message
) {
    public static SuccessRes from(HttpStatus status, String message) {
        return SuccessRes.builder()
                .status(status)
                .message(message)
                .build();
    }
}
