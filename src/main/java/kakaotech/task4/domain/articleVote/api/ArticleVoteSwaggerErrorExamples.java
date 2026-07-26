package kakaotech.task4.domain.articleVote.api;

public final class ArticleVoteSwaggerErrorExamples {
    private ArticleVoteSwaggerErrorExamples() {}

    public static final String GLOBAL_422_001 = """
            {
                "status": "UNPROCESSABLE_ENTITY",
                "success": false,
                "message": "필드의 유효성 검사가 올바르지 않습니다.",
                "code": "GLOBAL-422-001",
                "fields": {
                    "option": "선택지를 선택해주세요."
                }
            }
            """;

    public static final String COMMON_409 = """
            {
                "status": "CONFLICT",
                "success": false,
                "message": "데이터 정합성 제약을 위반했습니다.",
                "code": "COMMON_409"
            }
            """;
}
