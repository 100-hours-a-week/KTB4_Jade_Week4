package kakaotech.task4.common.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.token.code.JwtExceptionCode;
import kakaotech.task4.common.security.properties.JwtProperties;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthService {
    private final AccessTokenProvider accessTokenProvider;
    private final JwtProperties jwtProperties;
    private final MemberService memberService;

    public Authentication authenticate(String accessToken) {
        try {
            Claims claims = accessTokenProvider.getClaims(accessToken);
            validateClaims(claims);

            Member member = memberService.findByUuid(claims.getSubject(), AuthExceptionCode.UNAUTHORIZED);
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

            return UsernamePasswordAuthenticationToken.authenticated(member.getMemberUuid(), null, authorities);
        } catch (ExpiredJwtException e) {
            throw new CustomException(JwtExceptionCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(JwtExceptionCode.INVALID_ACCESS_TOKEN);
        }
    }

    private void validateClaims(Claims claims) {
        if (claims.getAudience() == null || !claims.getAudience().contains(jwtProperties.audience())) {
            throw new CustomException(JwtExceptionCode.INVALID_ACCESS_TOKEN);
        }

        if (!AccessTokenProvider.ACCESS_TOKEN_TYPE.equals(
                claims.get(AccessTokenProvider.TOKEN_TYPE_CLAIM, String.class))) {
            throw new CustomException(JwtExceptionCode.INVALID_ACCESS_TOKEN);
        }

        if (claims.getSubject() == null || claims.getSubject().isBlank()) {
            throw new CustomException(JwtExceptionCode.INVALID_ACCESS_TOKEN);
        }

        if (claims.getId() == null || claims.getId().isBlank()) {
            throw new CustomException(JwtExceptionCode.INVALID_ACCESS_TOKEN);
        }
    }
}