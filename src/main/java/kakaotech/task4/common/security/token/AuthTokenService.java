package kakaotech.task4.common.security.token;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.cookie.TokenCookieService;
import kakaotech.task4.common.security.properties.CookieProperties;
import kakaotech.task4.common.security.token.code.JwtExceptionCode;
import kakaotech.task4.common.security.token.dto.AccessTokenInfo;
import kakaotech.task4.common.security.token.dto.RefreshTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AuthTokenService {
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final TokenCookieService tokenCookieService;
    private final CookieProperties cookieProperties;

    public AccessTokenInfo issue(String memberUuid, HttpServletResponse response) {
        AccessTokenInfo accessTokenInfo = accessTokenProvider.createAccessToken(memberUuid);
        String refreshToken = refreshTokenProvider.createRefreshToken();
        String refreshTokenHash = refreshTokenProvider.hash(refreshToken);

        refreshTokenStore.save(refreshTokenHash, memberUuid);
        tokenCookieService.addAccessTokenCookie(response, accessTokenInfo.token());
        tokenCookieService.addRefreshTokenCookie(response, refreshToken);

        return accessTokenInfo;
    }

    public AccessTokenInfo reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);
        String refreshTokenHash = refreshTokenProvider.hash(refreshToken);
        RefreshTokenInfo refreshTokenInfo = refreshTokenStore.get(refreshTokenHash);

        refreshTokenStore.delete(refreshTokenHash);
        return issue(refreshTokenInfo.memberUuid(), response);
    }

    public void delete(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);
        String refreshTokenHash = refreshTokenProvider.hash(refreshToken);

        refreshTokenStore.delete(refreshTokenHash);
        tokenCookieService.deleteTokenCookies(response);
    }

    private String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new CustomException(JwtExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieProperties.refreshName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new CustomException(JwtExceptionCode.REFRESH_TOKEN_NOT_FOUND));
    }
}