package kakaotech.task4.domain.comment.api;

public final class CommentSwaggerErrorExamples {
    private CommentSwaggerErrorExamples() {}

    public static final String COMMENT_400_001 = """
            {
                "status": "BAD_REQUEST",
                "code": "COMMENT-400-001",
                "message": "필수 값이 제외되었습니다."
            }
            """;

    public static final String COMMENT_403_001 = """
            {
                "status": "FORBIDDEN",
                "code": "COMMENT-403-001",
                "message": "수정 권한이 없습니다."
            }
            """;

    public static final String COMMENT_403_002 = """
        {
            "status": "FORBIDDEN",
            "code": "COMMENT-403-002",
            "message": "삭제 권한이 없습니다."
        }
        """;

    public static final String COMMENT_404_001 = """
            {
                "status": "NOT_FOUND",
                "code": "COMMENT-404-001",
                "message": "해당 댓글을 찾을 수 없습니다."
            }
            """;
}