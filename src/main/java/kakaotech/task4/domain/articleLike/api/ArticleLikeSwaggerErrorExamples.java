package kakaotech.task4.domain.articleLike.api;

public final class ArticleLikeSwaggerErrorExamples {
    private ArticleLikeSwaggerErrorExamples() {}

    public static final String ARTICLE_LIKE_400_001 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "좋아요를 누르지 않은 게시글입니다.",
                "code": "ARTICLE_LIKE-400-001"
            }
            """;

    public static final String COMMON_409 = """
            {
                "status": "CONFLICT",
                "success": false,
                "message": "데이터 정합성 제약을 위반했습니다.",
                "code": "COMMON_409"
            }
            """;
}
