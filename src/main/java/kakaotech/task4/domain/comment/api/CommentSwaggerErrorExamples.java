package kakaotech.task4.domain.comment.api;

public final class CommentSwaggerErrorExamples {
    private CommentSwaggerErrorExamples() {}

    public static final String CREATE_COMMENT_400 = """
            {
                "status": "BAD_REQUEST",
                "code": "COMMENT-400-001",
                "message": "필수 값이 제외되었습니다."
            }
            """;

    public static final String CREATE_COMMENT_401 = """
            {
                "status": "UNAUTHORIZED",
                "code": "AUTH-401-001",
                "message": "로그인 후 사용할 수 있습니다."
            }
            """;

    public static final String CREATE_COMMENT_404 = """
            {
                "status": "NOT_FOUND",
                "code": "ARTICLE-404-001",
                "message": "해당 게시글을 찾을 수 없습니다."
            }
            """;
}