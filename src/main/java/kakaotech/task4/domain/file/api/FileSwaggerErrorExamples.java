package kakaotech.task4.domain.file.api;

public final class FileSwaggerErrorExamples {
    private FileSwaggerErrorExamples() {}

    public static final String FILE_400_001 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "지원하지 않는 이미지 형식입니다.",
                "code": "FILE-400-001"
            }
            """;

    public static final String FILE_400_002 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "올바르지 않은 프로필 이미지 주소입니다.",
                "code": "FILE-400-002"
            }
            """;

    public static final String FILE_429_001 = """
            {
                "status": "TOO_MANY_REQUESTS",
                "success": false,
                "message": "업로드 요청이 너무 많습니다. 잠시 후 다시 시도해주세요.",
                "code": "FILE-429-001"
            }
            """;

    public static final String GLOBAL_400_001 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "필수 값이 제외되었습니다.",
                "code": "GLOBAL-400-001",
                "fields": {
                    "contentType": "이미지 형식을 입력해주세요."
                }
            }
            """;
}
