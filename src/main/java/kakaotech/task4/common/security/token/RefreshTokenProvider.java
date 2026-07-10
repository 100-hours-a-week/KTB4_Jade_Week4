package kakaotech.task4.common.security.token;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.token.code.JwtExceptionCode;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class RefreshTokenProvider {
    private static final int REFRESH_TOKEN_BYTE_SIZE = 32;
    private final SecureRandom secureRandom = new SecureRandom();

    public String createRefreshToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTE_SIZE];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(JwtExceptionCode.TOKEN_HASH_FAILED);
        }
    }
}