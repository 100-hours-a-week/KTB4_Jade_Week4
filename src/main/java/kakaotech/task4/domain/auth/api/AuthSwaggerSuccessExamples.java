package kakaotech.task4.domain.auth.api;

public final class AuthSwaggerSuccessExamples {
    private AuthSwaggerSuccessExamples() {}

    public static final String SIGN_UP_201 = """
            {
              "status": "CREATED",
              "success": true,
              "message": "회원가입이 완료되었습니다."
            }
            """;

    public static final String SIGN_IN_200 = """
            {
              "status": "OK",
              "success": true,
              "data": {
                "profileImageUrl": "url",
                "accessTokenExpiresAt": "2026-07-11T12:34:56Z"
              }
            }
            """;

    public static final String TOKEN_REISSUE_200 = """
            {
              "status": "OK",
              "success": true,
              "data": {
                "accessTokenExpiresAt": "2026-07-11T12:34:56Z"
              }
            }
            """;
}
