package kakaotech.task4.security;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.resolver.CurrentMemberArgumentResolver;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentMemberArgumentResolverTest {
    private final CurrentMemberArgumentResolver resolver = new CurrentMemberArgumentResolver();

    private final MethodParameter principalParameter =
            methodParameter("principal", AuthenticatedMember.class);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityContext에 인증 정보가 없으면 UNAUTHORIZED 예외가 발생한다")
    void resolveArgumentWithoutAuthentication() {
        assertThatThrownBy(() -> resolver.resolveArgument(principalParameter, null, null, null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("principal이 AuthenticatedMember가 아니면 UNAUTHORIZED 예외가 발생한다")
    void resolveArgumentWithInvalidPrincipal() {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                new Object(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        setAuthentication(authentication);

        assertThatThrownBy(() -> resolver.resolveArgument(principalParameter, null, null, null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("AuthenticatedMember 파라미터는 DB 조회 없이 principal을 반환한다")
    void resolvePrincipalWithoutMemberLookup() {
        AuthenticatedMember principal = new AuthenticatedMember("member_uuid");
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        setAuthentication(authentication);

        Object resolved = resolver.resolveArgument(principalParameter, null, null, null);

        assertThat(resolved).isSameAs(principal);
    }

    @Test
    @DisplayName("AuthenticatedMember 파라미터만 지원한다")
    void supportsOnlyAuthenticatedMemberParameter() {
        assertThat(resolver.supportsParameter(principalParameter)).isTrue();
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static MethodParameter methodParameter(String name, Class<?> type) {
        try {
            return new MethodParameter(TestController.class.getDeclaredMethod(name, type), 0);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class TestController {
        void principal(@CurrentMember AuthenticatedMember principal) {
        }
    }
}
