package kakaotech.task4.common.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kakaotech.task4.common.security.properties.JwtProperties;
import kakaotech.task4.common.security.token.dto.AccessTokenInfo;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class AccessTokenProvider {
    public static final String TOKEN_TYPE_CLAIM = "token_type";
    public static final String ACCESS_TOKEN_TYPE = "access";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public AccessTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public AccessTokenInfo createAccessToken(String memberUuid) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.accessExpiration());

        String token = Jwts.builder()
                .issuer(jwtProperties.issuer())
                .audience().add(jwtProperties.audience()).and()
                .subject(memberUuid)
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        return AccessTokenInfo.of(token, expiresAt);
    }

    public Claims getClaims(String accessToken) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.issuer())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
    }

    public String getMemberUuid(String accessToken) {
        return getClaims(accessToken).getSubject();
    }
}