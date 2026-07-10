package kakaotech.task4.domain.auth.api;

public final class AuthSwaggerSuccessExamples {
    private AuthSwaggerSuccessExamples() {}

    public static final String SIGN_UP_201 = """
            {
              "status": "CREATED",
              "message": "회원가입이 완료되었습니다."
            }
            """;

    public static final String SIGN_IN_200 = """
            {
              "profileImageUrl": "url",
              "accessTokenExpiresAt": "2026-07-11T12:34:56Z"
            }
            """;

    public static final String TOKEN_REISSUE_200 = """
            {
              "accessTokenExpiresAt": "2026-07-11T12:34:56Z"
            }
            """;
}