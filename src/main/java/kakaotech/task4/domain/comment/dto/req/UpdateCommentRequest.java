package kakaotech.task4.domain.comment.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(
        @Schema(description = "댓글 내용", example = "댓글 수정")
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {}