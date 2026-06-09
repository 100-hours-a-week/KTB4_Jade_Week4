package kakaotech.task4.domain.articleLike.dto.res;

import lombok.Builder;

@Builder
public record ArticleLikeResponse(
        boolean isLiked,
        int likeCount
) {
    public static ArticleLikeResponse of(boolean isLiked, int likeCount) {
        return ArticleLikeResponse.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}