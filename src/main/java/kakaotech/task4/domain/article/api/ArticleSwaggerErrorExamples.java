package kakaotech.task4.domain.article.api;

public final class ArticleSwaggerErrorExamples {
    private ArticleSwaggerErrorExamples(){}

    public static final String CREATE_ARTICLE_400 = """
            {
                "status": "BAD_REQUEST",
                "code": "ARTICLE-400-001",
                "message": "필수 값이 제외되었습니다.",
                "fields": {
                    "title": "제목을 입력해주세요.",
                    "content": "내용을 입력해주세요."
                }
            }
            """;

    public static final String CREATE_ARTICLE_401 = """
            {
                "status": "UNAUTHORIZED",
                "code": "ARTICLE-401-001",
                "message": "로그인 후 사용할 수 있습니다."
            }
            """;

    public static final String CREATE_ARTICLE_422 = """
            {
                "status": "UNPROCESSABLE_ENTITY",
                "code": "ARTICLE-422-001",
                "message": "필드의 유효성 검사가 올바르지 않습니다.",
                "fields": {
                    "title": "제목의 최대 길이는 26자 입니다."
                }
            }
            """;
}
