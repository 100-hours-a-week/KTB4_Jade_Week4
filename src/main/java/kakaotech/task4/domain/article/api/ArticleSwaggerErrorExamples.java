package kakaotech.task4.domain.article.api;

public final class ArticleSwaggerErrorExamples {
    private ArticleSwaggerErrorExamples() {}

    public static final String GLOBAL_400_001 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "필수 값이 제외되었습니다.",
                "code": "GLOBAL-400-001",
                "fields": {
                    "title": "제목을 입력해주세요.",
                    "optionA": "선택지 A를 입력해주세요.",
                    "optionB": "선택지 B를 입력해주세요."
                }
            }
            """;

    public static final String ARTICLE_400_001 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "변경할 내용을 입력해주세요",
                "code": "ARTICLE-400-001"
            }
            """;

    public static final String ARTICLE_403_001 = """
            {
                "status": "FORBIDDEN",
                "success": false,
                "message": "수정 권한이 없습니다.",
                "code": "ARTICLE-403-001"
            }
            """;

    public static final String ARTICLE_403_002 = """
            {
                "status": "FORBIDDEN",
                "success": false,
                "message": "삭제 권한이 없습니다.",
                "code": "ARTICLE-403-002"
            }
            """;

    public static final String ARTICLE_404_001 = """
            {
                "status": "NOT_FOUND",
                "success": false,
                "message": "해당 게시글을 찾을 수 없습니다.",
                "code": "ARTICLE-404-001"
            }
            """;

    public static final String GLOBAL_422_001 = """
            {
                "status": "UNPROCESSABLE_ENTITY",
                "success": false,
                "message": "필드의 유효성 검사가 올바르지 않습니다.",
                "code": "GLOBAL-422-001",
                "fields": {
                    "title": "제목의 최대 길이는 26자 입니다.",
                    "optionA": "선택지 A의 최대 길이는 15자 입니다."
                }
            }
            """;

    public static final String GLOBAL_422_001_SIZE = """
            {
                "status": "UNPROCESSABLE_ENTITY",
                "success": false,
                "message": "필드의 유효성 검사가 올바르지 않습니다.",
                "code": "GLOBAL-422-001",
                "fields": {
                    "size": "조회 개수는 최대 10개입니다."
                }
            }
            """;
}
