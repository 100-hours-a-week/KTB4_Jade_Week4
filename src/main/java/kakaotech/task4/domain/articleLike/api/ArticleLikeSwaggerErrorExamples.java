package kakaotech.task4.domain.articleLike.api;

public final class ArticleLikeSwaggerErrorExamples {
    private ArticleLikeSwaggerErrorExamples() {}

    public static final String ARTICLE_LIKE_409_001 = """
            {
                "status": "CONFLICT",
                "code": "ARTICLE_LIKE-409-001",
                "message": "좋아요를 누르지 않은 게시글입니다."
            }
            """;
}