package kakaotech.task4.domain.articleLike.api;

public final class ArticleLikeSwaggerSuccessExamples {
    private ArticleLikeSwaggerSuccessExamples() {}

    public static final String LIKE_201 = """
            {
                "isLiked": true,
                "likeCount": 4
            }
            """;

    public static final String UNLIKE_201 = """
            {
                "isLiked": false,
                "likeCount": 3
            }
            """;
}