package kakaotech.task4.common.security.cookie;

import jakarta.servlet.http.HttpServletResponse;
import kakaotech.task4.common.security.properties.CookieProperties;
import kakaotech.task4.common.security.properties.JwtProperties;
import kakaotech.task4.common.security.properties.RefreshTokenProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenCookieService {
    private static final String ACCESS_COOKIE_PATH = "/";
    private static final String REFRESH_COOKIE_PATH = "/auth";

    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;
    private final RefreshTokenProperties refreshTokenProperties;

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        addCookie(response, cookieProperties.accessName(), accessToken, ACCESS_COOKIE_PATH, jwtProperties.accessExpiration());
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addCookie(response, cookieProperties.refreshName(), refreshToken, REFRESH_COOKIE_PATH, refreshTokenProperties.expiration());
    }

    public void deleteTokenCookies(HttpServletResponse response) {
        deleteCookie(response, cookieProperties.accessName(), ACCESS_COOKIE_PATH);
        deleteCookie(response, cookieProperties.refreshName(), REFRESH_COOKIE_PATH);
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