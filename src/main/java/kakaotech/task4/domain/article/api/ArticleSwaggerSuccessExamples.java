package kakaotech.task4.domain.article.api;

public final class ArticleSwaggerSuccessExamples {
    private ArticleSwaggerSuccessExamples(){}

    public static final String CREATE_ARTICLE_201 = """
            {
                "articleUuid": "articleUuid"
            }
            """;

    public static final String ARTICLE_200_001 = """
        {
            "articles": [
                {
                    "articleUuid": "articleUuid1",
                    "title": "제목입니다.",
                    "likeCount": 0,
                    "commentCount": 0,
                    "viewCount": 0,
                    "createdAt": "2026-01-01T09:00:00Z",
                    "writer": "작성자1",
                    "profileImageUrl": "프로필이미지url"
                },
                {
                    "articleUuid": "articleUuid2",
                    "title": "두 번째 제목입니다.",
                    "likeCount": 0,
                    "commentCount": 0,
                    "viewCount": 0,
                    "createdAt": "2026-01-01T09:00:00Z",
                    "writer": "작성자2",
                    "profileImageUrl": "프로필이미지url"
                }
            ],
            "hasNext": true,
            "nextCursor": "articleUuid2"
        }
        """;

}
