package kakaotech.task4.common.security.cookie;

import jakarta.servlet.http.HttpServletResponse;
import kakaotech.task4.common.security.properties.CookieProperties;
import kakaotech.task4.common.security.properties.JwtProperties;
import kakaotech.task4.common.security.properties.RefreshTokenProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenCookieService {
    private static final String ACCESS_COOKIE_PATH = "/";
    private static final String REFRESH_COOKIE_SUFFIX = "/auth";

    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;
    private final RefreshTokenProperties refreshTokenProperties;

    /**
     * refresh 토큰은 재발급·로그아웃 요청에만 실리도록 경로를 좁힌다.
     * context-path가 있으면 그 아래로 잡아야 브라우저가 쿠키를 함께 보낸다.
     */
    private final String refreshCookiePath;

    public TokenCookieService(CookieProperties cookieProperties,
                              JwtProperties jwtProperties,
                              RefreshTokenProperties refreshTokenProperties,
                              @Value("${server.servlet.context-path:}") String contextPath) {
        this.cookieProperties = cookieProperties;
        this.jwtProperties = jwtProperties;
        this.refreshTokenProperties = refreshTokenProperties;
        this.refreshCookiePath = contextPath + REFRESH_COOKIE_SUFFIX;
    }

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        addCookie(response, cookieProperties.accessName(), accessToken, ACCESS_COOKIE_PATH, jwtProperties.accessExpiration());
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addCookie(response, cookieProperties.refreshName(), refreshToken, refreshCookiePath, refreshTokenProperties.expiration());
    }

    public void deleteTokenCookies(HttpServletResponse response) {
        deleteCookie(response, cookieProperties.accessName(), ACCESS_COOKIE_PATH);
        deleteCookie(response, cookieProperties.refreshName(), refreshCookiePath);
    }

    private void addCookie(HttpServletResponse response, String name, String value, String path, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .path(path)
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void deleteCookie(HttpServletResponse response, String name, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .path(path)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}