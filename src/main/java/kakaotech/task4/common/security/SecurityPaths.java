package kakaotech.task4.common.security;

import java.util.Set;

public final class SecurityPaths {
    public static final String SIGN_UP = "/auth/sign-up";
    public static final String SIGN_IN = "/auth/sign-in";
    public static final String TOKEN_REISSUE = "/auth/token/re-issue";
    public static final String CSRF = "/auth/csrf";

    public static final String[] PUBLIC_PATHS = {
            SIGN_UP,
            SIGN_IN,
            TOKEN_REISSUE,
            CSRF,
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    public static final Set<String> JWT_EXCLUDED_PATHS = Set.of(
            SIGN_UP,
            SIGN_IN,
            TOKEN_REISSUE,
            CSRF
    );

    private SecurityPaths() {
    }
}