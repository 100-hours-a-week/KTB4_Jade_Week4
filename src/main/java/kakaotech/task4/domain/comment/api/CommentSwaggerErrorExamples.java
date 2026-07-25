package kakaotech.task4.domain.comment.api;

public final class CommentSwaggerErrorExamples {
    private CommentSwaggerErrorExamples() {}

    public static final String GLOBAL_400_001 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "필수 값이 제외되었습니다.",
                "code": "GLOBAL-400-001",
                "fields": {
                    "content": "내용을 입력해주세요."
                }
            }
            """;

    public static final String COMMENT_403_001 = """
            {
                "status": "FORBIDDEN",
                "success": false,
                "message": "수정 권한이 없습니다.",
                "code": "COMMENT-403-001"
            }
            """;

    public static final String COMMENT_403_002 = """
            {
                "status": "FORBIDDEN",
                "success": false,
                "message": "삭제 권한이 없습니다.",
                "code": "COMMENT-403-002"
            }
            """;

    public static final String COMMENT_404_001 = """
            {
                "status": "NOT_FOUND",
                "success": false,
                "message": "해당 댓글을 찾을 수 없습니다.",
                "code": "COMMENT-404-001"
            }
            """;
}
