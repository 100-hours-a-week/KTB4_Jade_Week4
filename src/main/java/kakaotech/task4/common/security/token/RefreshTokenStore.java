package kakaotech.task4.common.security.token;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.properties.RefreshTokenProperties;
import kakaotech.task4.common.security.token.code.JwtExceptionCode;
import kakaotech.task4.common.security.token.dto.RefreshTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {
    private final RefreshTokenProperties refreshTokenProperties;
    private final ConcurrentHashMap<String, RefreshTokenInfo> store = new ConcurrentHashMap<>();

    public void save(String refreshTokenHash, String memberUuid) {
        Instant expiresAt = Instant.now().plus(refreshTokenProperties.expiration());
        store.put(refreshTokenHash, RefreshTokenInfo.of(memberUuid, expiresAt));
    }

    public RefreshTokenInfo get(String refreshTokenHash) {
        RefreshTokenInfo refreshTokenInfo = Optional.ofNullable(store.get(refreshTokenHash))
                .orElseThrow(() -> new CustomException(JwtExceptionCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshTokenInfo.isExpired()) {
            store.remove(refreshTokenHash);
            throw new CustomException(JwtExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        return refreshTokenInfo;
    }

    public void delete(String refreshTokenHash) {
        store.remove(refreshTokenHash);
    }
}