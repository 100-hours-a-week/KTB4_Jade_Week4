package kakaotech.task4.domain.file.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlResponse(
        @Schema(description = "이미지를 PUT으로 올릴 임시 URL")
        String uploadUrl,

        @Schema(description = "업로드 완료 후 프로필에 저장할 공개 URL")
        String fileUrl
) {
}
