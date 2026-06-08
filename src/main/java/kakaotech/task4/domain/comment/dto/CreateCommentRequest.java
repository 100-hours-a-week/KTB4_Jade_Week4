package kakaotech.task4.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @Schema(description = "댓글 내용", example = "댓글 작성")
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {}