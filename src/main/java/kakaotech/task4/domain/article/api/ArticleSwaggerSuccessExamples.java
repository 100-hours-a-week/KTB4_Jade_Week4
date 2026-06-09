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

    public static final String ARTICLE_200_002 = """
        {
            "title": "제목입니다.",
            "writer": "게시글 작성자1",
            "userUuid": "userUuid",
            "createdAt": "2026-01-01T09:00:00Z",
            "imageUrl": "본문 이미지url",
            "content": "내용",
            "likeCount": 0,
            "viewCount": 0,
            "commentCount": 0,
            "isLiked": false,
            "comments": [
                {
                    "commentUuid": "commentUuid",
                    "writer": "댓글 작성자1",
                    "profileImageUrl": "프로필 이미지url",
                    "userUuid": "userUuid",
                    "createdAt": "2026-01-01T09:00:00Z",
                    "content": "댓글내용"
                }
            ]
        }
        """;

}
