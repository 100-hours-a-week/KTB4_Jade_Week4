package kakaotech.task4.domain.file.code;

import kakaotech.task4.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FileExceptionCode implements ExceptionCode {
    UNSUPPORTED_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "FILE-400-001", "지원하지 않는 이미지 형식입니다."),
    INVALID_PROFILE_IMAGE_URL(HttpStatus.BAD_REQUEST, "FILE-400-002", "올바르지 않은 프로필 이미지 주소입니다."),
    TOO_MANY_UPLOAD_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "FILE-429-001", "업로드 요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
