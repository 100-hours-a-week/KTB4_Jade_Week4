package kakaotech.task4.security;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.resolver.CurrentMemberArgumentResolver;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentMemberArgumentResolverTest {
    @Mock
    private MemberService memberService;
    @InjectMocks
    private CurrentMemberArgumentResolver resolver;
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityContext에 인증 정보가 없으면 UNAUTHORIZED 예외가 발생한다")
    void resolveArgumentWithoutAuthentication() {
        assertThatThrownBy(() -> resolver.resolveArgument(null, null, null, null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("principal이 String이 아니면 UNAUTHORIZED 예외가 발생한다")
    void resolveArgumentWithNonStringPrincipal() {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                new Object(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        setAuthentication(authentication);

        assertThatThrownBy(() -> resolver.resolveArgument(null, null, null, null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("정상 인증된 요청이면 principal의 UUID로 회원을 조회해 반환한다")
    void resolveArgumentSuccess() {
        Member member = createMember();
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                member.getMemberUuid(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        setAuthentication(authentication);
        when(memberService.findByUuid(member.getMemberUuid(), AuthExceptionCode.UNAUTHORIZED)).thenReturn(member);

        Object resolved = resolver.resolveArgument(null, null, null, null);

        assertThat(resolved).isEqualTo(member);
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
