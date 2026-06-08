package kakaotech.task4.domain.comment.dto.res;

import lombok.Builder;

@Builder
public record CreateCommentResponse(
        String commentUuid
) {
    public static CreateCommentResponse from(String commentUuid) {
        return CreateCommentResponse.builder()
                .commentUuid(commentUuid)
                .build();
    }
}