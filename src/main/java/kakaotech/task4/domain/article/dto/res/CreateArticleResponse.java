package kakaotech.task4.domain.article.dto.res;

import lombok.Builder;

@Builder
public record CreateArticleResponse (
        String articleUuid
) {
    public static CreateArticleResponse from(String articleUuid) {
        return CreateArticleResponse.builder()
                .articleUuid(articleUuid)
                .build();
    }
}
