package kakaotech.task4.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.token.AuthTokenService;
import kakaotech.task4.common.security.token.dto.AccessTokenInfo;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.auth.dto.req.SignInRequest;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
import kakaotech.task4.domain.auth.dto.res.SignInResponse;
import kakaotech.task4.domain.auth.dto.res.TokenReissueResponse;
import kakaotech.task4.domain.auth.service.AuthService;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private MemberService memberService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthTokenService authTokenService;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 다르면 VALIDATION_ERROR 예외가 발생하고 회원가입되지 않는다")
    void signUpWithPasswordMismatch() {
        // given
        SignUpRequest request = createSignUpRequest("jade@example.com", "Password123!", "Password1234!", "jade");

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.VALIDATION_ERROR);

        verify(memberService, never()).signUp(any());
    }

    @Test
    @DisplayName("이메일이 중복되면 CONFLICT 예외가 발생하고 회원가입되지 않는다")
    void signUpWithDuplicateEmail() {
        // given
        SignUpRequest request = createSignUpRequest("jade@example.com", "Password123!", "Password123!", "jade");
        when(memberService.existsByEmail("jade@example.com")).thenReturn(true);
        when(memberService.existsByNickname("jade")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.CONFLICT);

        verify(memberService, never()).signUp(any());
    }

    @Test
    @DisplayName("닉네임이 중복되면 CONFLICT 예외가 발생하고 회원가입되지 않는다")
    void signUpWithDuplicateNickname() {
        // given
        SignUpRequest request = createSignUpRequest("jade@example.com", "Password123!", "Password123!", "jade");
        when(memberService.existsByEmail("jade@example.com")).thenReturn(false);
        when(memberService.existsByNickname("jade")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.CONFLICT);

        verify(memberService, never()).signUp(any());
    }

    @Test
    @DisplayName("중복이 없고 비밀번호가 일치하면 회원가입에 성공한다")
    void signUpSuccess() {
        // given
        SignUpRequest request = createSignUpRequest("jade@example.com", "Password123!", "Password123!", "jade");
        when(memberService.existsByEmail("jade@example.com")).thenReturn(false);
        when(memberService.existsByNickname("jade")).thenReturn(false);

        // when
        authService.signUp(request);

        // then
        verify(memberService).signUp(request);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인하면 INVALID_CREDENTIALS 예외가 발생한다")
    void signInWithNonExistentEmail() {
        // given
        SignInRequest request = new SignInRequest("jade@example.com", "Password123!");
        HttpServletResponse response = mockResponse();
        when(memberService.findByEmail("jade@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.signIn(request, response))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 INVALID_CREDENTIALS 예외가 발생한다")
    void signInWithWrongPassword() {
        // given
        Member member = createMember();
        SignInRequest request = new SignInRequest("jade@example.com", "WrongPassword1!");
        HttpServletResponse response = mockResponse();
        when(memberService.findByEmail("jade@example.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("WrongPassword1!", member.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signIn(request, response))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", AuthExceptionCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("이메일과 비밀번호가 일치하면 로그인에 성공하고 토큰이 발급된다")
    void signInSuccess() {
        // given
        Member member = createMember();
        SignInRequest request = new SignInRequest("jade@example.com", "Password123!");
        HttpServletResponse response = mockResponse();
        Instant expiresAt = Instant.now().plusSeconds(3600);

        when(memberService.findByEmail("jade@example.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("Password123!", member.getPassword())).thenReturn(true);
        when(authTokenService.issue(member.getMemberUuid(), response))
                .thenReturn(AccessTokenInfo.of("access-token", expiresAt));

        // when
        SignInResponse signInResponse = authService.signIn(request, response);

        // then
        assertThat(signInResponse.profileImageUrl()).isEqualTo(member.getProfileImageUrl());
        assertThat(signInResponse.accessTokenExpiresAt()).isEqualTo(expiresAt);
        verify(authTokenService).issue(member.getMemberUuid(), response);
    }

    @Test
    @DisplayName("로그아웃하면 토큰 삭제가 위임된다")
    void signOutDelegatesToAuthTokenService() {
        // given
        HttpServletRequest request = mockRequest();
        HttpServletResponse response = mockResponse();

        // when
        authService.signOut(request, response);

        // then
        verify(authTokenService).delete(request, response);
    }

    @Test
    @DisplayName("토큰 재발급을 요청하면 새 만료 시각을 담아 응답한다")
    void reissueSuccess() {
        // given
        HttpServletRequest request = mockRequest();
        HttpServletResponse response = mockResponse();
        Instant expiresAt = Instant.now().plusSeconds(3600);
        when(authTokenService.reissue(request, response))
                .thenReturn(AccessTokenInfo.of("new-access-token", expiresAt));

        // when
        TokenReissueResponse reissueResponse = authService.reissue(request, response);

        // then
        assertThat(reissueResponse.accessTokenExpiresAt()).isEqualTo(expiresAt);
    }

    private SignUpRequest createSignUpRequest(String email, String password, String checkPassword, String nickname) {
        return new SignUpRequest(email, password, checkPassword, nickname, "https://example.com/profile.png");
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

    private HttpServletRequest mockRequest() {
        return org.mockito.Mockito.mock(HttpServletRequest.class);
    }

    private HttpServletResponse mockResponse() {
        return org.mockito.Mockito.mock(HttpServletResponse.class);
    }
}
