package kakaotech.task4.domain.article.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateArticleRequest(
        @Schema(description = "제목", example = "제목입니다.")
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 26, message = "제목의 최대 길이는 26자 입니다.")
        String title,

        @Schema(description = "선택지 A", example = "치킨")
        @NotBlank(message = "선택지 A를 입력해주세요.")
        @Size(max = 15, message = "선택지 A의 최대 길이는 15자 입니다.")
        String optionA,

        @Schema(description = "선택지 B", example = "피자")
        @NotBlank(message = "선택지 B를 입력해주세요.")
        @Size(max = 15, message = "선택지 B의 최대 길이는 15자 입니다.")
        String optionB
) {}