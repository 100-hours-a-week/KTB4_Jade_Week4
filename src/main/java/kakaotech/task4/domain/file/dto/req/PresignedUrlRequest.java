package kakaotech.task4.domain.file.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequest(
        @Schema(description = "업로드할 이미지의 MIME 타입", example = "image/webp")
        @NotBlank(message = "이미지 형식을 입력해주세요.")
        String contentType
) {
}
