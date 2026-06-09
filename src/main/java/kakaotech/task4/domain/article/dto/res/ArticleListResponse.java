package kakaotech.task4.domain.article.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record ArticleListResponse(
        List<ArticleSummaryResponse> articles,
        boolean hasNext,
        String nextCursor
) {
    public static ArticleListResponse of(List<ArticleSummaryResponse> articles, boolean hasNext, String nextCursor) {
        return ArticleListResponse.builder()
                .articles(articles)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }
}
