package kakaotech.task4.domain.articleLike.api;

public final class ArticleLikeSwaggerSuccessExamples {
    private ArticleLikeSwaggerSuccessExamples() {}

    public static final String LIKE_200 = """
            {
                "status": "OK",
                "success": true,
                "data": {
                    "isLiked": true,
                    "likeCount": 4
                }
            }
            """;

    public static final String UNLIKE_200 = """
            {
                "status": "OK",
                "success": true,
                "data": {
                    "isLiked": false,
                    "likeCount": 3
                }
            }
            """;
}
