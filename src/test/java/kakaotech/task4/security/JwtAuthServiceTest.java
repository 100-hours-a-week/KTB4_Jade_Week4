package kakaotech.task4.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.properties.JwtProperties;
import kakaotech.task4.common.security.token.AccessTokenProvider;
import kakaotech.task4.common.security.token.JwtAuthService;
import kakaotech.task4.common.security.token.code.JwtExceptionCode;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthServiceTest {
    private static final String AUDIENCE = "task4-client";
    private static final String OTHER_AUDIENCE = "other-client";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private MemberService memberService;

    private final JwtProperties jwtProperties = new JwtProperties("secret", "task4-issuer", AUDIENCE, Duration.ofMinutes(30));

    private JwtAuthService jwtAuthService;

    @BeforeEach
    void setUp() {
        jwtAuthService = new JwtAuthService(accessTokenProvider, jwtProperties, memberService);
    }

    @Test
    @DisplayName("유효한 Access Token이면 회원 UUID를 principal로, ROLE_USER 권한을 가진 인증 객체를 반환한다")
    void authenticateSuccess() {
        // given
        Member member = createMember();
        Claims claims = createValidClaims(member.getMemberUuid());
        when(accessTokenProvider.getClaims("valid-token")).thenReturn(claims);
        when(memberService.findByUuid(member.getMemberUuid(), AuthExceptionCode.UNAUTHORIZED)).thenReturn(member);

        // when
        Authentication authentication = jwtAuthService.authenticate("valid-token");

        // then
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo(member.getMemberUuid());
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("만료된 Access Token이면 ACCESS_TOKEN_EXPIRED 예외가 발생한다")
    void authenticateWithExpiredToken() {
        // given
        when(accessTokenProvider.getClaims("expired-token"))
                .thenThrow(new ExpiredJwtException(null, null, "expired"));

        // when & then
        assertThatThrownBy(() -> jwtAuthService.authenticate("expired-token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", JwtExceptionCode.ACCESS_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("서명이 위조된 Access Token이면 INVALID_ACCESS_TOKEN 예외가 발생한다")
    void authenticateWithMalformedToken() {
        // given
        when(accessTokenProvider.getClaims("malformed-token"))
                .thenThrow(new MalformedJwtException("malformed"));

        // when & then
        assertThatThrownBy(() -> jwtAuthService.authenticate("malformed-token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", JwtExceptionCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("audience가 일치하지 않으면 INVALID_ACCESS_TOKEN 예외가 발생한다")
    void authenticateWithWrongAudience() {
        // given
        Claims claims = Jwts.claims()
                .subject("member_uuid")
                .id(UUID.randomUUID().toString())
                .audience().add(OTHER_AUDIENCE).and()
                .add(AccessTokenProvider.TOKEN_TYPE_CLAIM, AccessTokenProvider.ACCESS_TOKEN_TYPE)
                .build();
        when(accessTokenProvider.getClaims("token")).thenReturn(claims);

        // when & then
        assertThatThrownBy(() -> jwtAuthService.authenticate("token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", JwtExceptionCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("token_type이 access가 아니면 INVALID_ACCESS_TOKEN 예외가 발생한다")
    void authenticateWithWrongTokenType() {
        // given
        Claims claims = Jwts.claims()
                .subject("member_uuid")
                .id(UUID.randomUUID().toString())
                .audience().add(AUDIENCE).and()
                .add(AccessTokenProvider.TOKEN_TYPE_CLAIM, "refresh")
                .build();
        when(accessTokenProvider.getClaims("token")).thenReturn(claims);

        // when & then
        assertThatThrownBy(() -> jwtAuthService.authenticate("token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", JwtExceptionCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("subject가 없으면 INVALID_ACCESS_TOKEN 예외가 발생한다")
    void authenticateWithBlankSubject() {
        // given
        Claims claims = Jwts.claims()
                .id(UUID.randomUUID().toString())
                .audience().add(AUDIENCE).and()
                .add(AccessTokenProvider.TOKEN_TYPE_CLAIM, AccessTokenProvider.ACCESS_TOKEN_TYPE)
                .build();
        when(accessTokenProvider.getClaims("token")).thenReturn(claims);

        // when & then
        assertThatThrownBy(() -> jwtAuthService.authenticate("token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", JwtExceptionCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("jti가 없으면 INVALID_ACCESS_TOKEN 예외가 발생한다")
    void authenticateWithBlankJti() {
        // given
        Claims claims = Jwts.claims()
                .subject("member_uuid")
                .audience().add(AUDIENCE).and()
                .add(AccessTokenProvider.TOKEN_TYPE_CLAIM, AccessTokenProvider.ACCESS_TOKEN_TYPE)
                .build();
        when(accessTokenProvider.getClaims("token")).thenReturn(claims);

        // when & then
        assertThatThrownBy(() -> jwtAuthService.authenticate("token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", JwtExceptionCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("토큰은 유효하지만 대상 회원이 존재하지 않으면 UNAUTHORIZED 예외가 발생한다")
    void authenticateWithNonExistentMember() {
        // given
        Claims claims = createValidClaims("ghost_uuid");
        when(accessTokenProvider.getClaims("token")).thenReturn(claims);
        when(memberService.findByUuid("ghost_uuid", AuthExceptionCode.UNAUTHORIZED))
                .thenThrow(new CustomException(AuthExceptionCode.UNAUTHORIZED));

        // when & then
        assertThatThrownBy(() -> jwtAuthService.authenticate("token"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.UNAUTHORIZED);
    }

    private Claims createValidClaims(String memberUuid) {
        return Jwts.claims()
                .subject(memberUuid)
                .id(UUID.randomUUID().toString())
                .audience().add(AUDIENCE).and()
                .add(AccessTokenProvider.TOKEN_TYPE_CLAIM, AccessTokenProvider.ACCESS_TOKEN_TYPE)
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .memberUuid("member_uuid")
                .email("jade@example.com")
                .password("encodedPassword")
                .nickname("jade")
                .profileImageUrl("https://example.com/profile.png")
                .build();
    }
}
