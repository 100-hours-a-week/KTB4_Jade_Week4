package kakaotech.task4.domain.articleVote.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kakaotech.task4.domain.articleVote.entity.VoteOption;

public record CreateVoteRequest(
        @Schema(description = "선택지", example = "A")
        @NotNull(message = "선택지를 선택해주세요.")
        VoteOption option
) {}
