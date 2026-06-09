package kakaotech.task4.domain.article.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record UpdateArticleRequest(
        @Schema(description = "제목", example = "제목입니다.")
        @Size(max = 26, message = "제목의 최대 길이는 26자 입니다.")
        String title,

        @Schema(description = "내용", example = "내용입니다.")
        String content,

        @Schema(description = "이미지 URL", example = "이미지url")
        String imageUrl
) {}