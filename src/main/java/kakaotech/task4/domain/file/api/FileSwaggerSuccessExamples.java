package kakaotech.task4.domain.file.api;

public final class FileSwaggerSuccessExamples {
    private FileSwaggerSuccessExamples() {}

    public static final String FILE_200_001 = """
            {
                "status": "OK",
                "success": true,
                "data": {
                    "uploadUrl": "https://jade-dev.s3.ap-northeast-2.amazonaws.com/profile/3f2b.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Signature=...",
                    "fileUrl": "https://jade-dev.s3.ap-northeast-2.amazonaws.com/profile/3f2b.webp"
                }
            }
            """;
}
