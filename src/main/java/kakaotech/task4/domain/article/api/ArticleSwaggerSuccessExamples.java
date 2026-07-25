package kakaotech.task4.domain.article.api;

public final class ArticleSwaggerSuccessExamples {
    private ArticleSwaggerSuccessExamples(){}

    public static final String CREATE_ARTICLE_201 = """
            {
                "status": "CREATED",
                "success": true,
                "data": {
                    "articleUuid": "ak_1f0c9c6a5e8b4a1d9c3e7f2b6d4a8c05"
                }
            }
            """;

    public static final String ARTICLE_200_001 = """
        {
            "status": "OK",
            "success": true,
            "data": {
                "articles": [
                    {
                        "articleUuid": "ak_1f0c9c6a5e8b4a1d9c3e7f2b6d4a8c05",
                        "title": "제목입니다.",
                        "optionA": "치킨",
                        "optionB": "피자",
                        "voteCountA": 482,
                        "voteCountB": 732,
                        "myVote": "B",
                        "writer": "작성자1",
                        "isMine": true,
                        "profileImageUrl": "프로필이미지url",
                        "createdAt": "2026-01-01T09:00:00",
                        "likeCount": 129,
                        "isLiked": true
                    },
                    {
                        "articleUuid": "ak_7c3a8f1b2d4e5a6c9b0d1e2f3a4b5c6d",
                        "title": "두 번째 제목입니다.",
                        "optionA": "짜장면",
                        "optionB": "짬뽕",
                        "voteCountA": 0,
                        "voteCountB": 0,
                        "myVote": null,
                        "writer": "작성자2",
                        "isMine": false,
                        "profileImageUrl": "프로필이미지url",
                        "createdAt": "2026-01-01T08:00:00",
                        "likeCount": 0,
                        "isLiked": false
                    }
                ],
                "hasNext": true,
                "nextCursor": "MjAyNi0wMS0wMVQwODowMDowMHwxMg=="
            }
        }
        """;

    public static final String ARTICLE_200_002 = """
        {
            "status": "OK",
            "success": true,
            "data": {
                "title": "제목입니다.",
                "optionA": "치킨",
                "optionB": "피자",
                "voteCountA": 482,
                "voteCountB": 732,
                "myVote": "B",
                "writer": "게시글 작성자1",
                "isMine": true,
                "profileImageUrl": "프로필 이미지url",
                "createdAt": "2026-01-01T09:00:00",
                "likeCount": 129,
                "isLiked": true,
                "comments": [
                    {
                        "commentUuid": "ck_4d2e8a1b6c9f3d7a5b0e1c2f8a6d4b39",
                        "writer": "댓글 작성자1",
                        "profileImageUrl": "프로필 이미지url",
                        "isMine": false,
                        "createdAt": "2026-01-01T09:10:00",
                        "content": "댓글내용"
                    }
                ]
            }
        }
        """;

}
