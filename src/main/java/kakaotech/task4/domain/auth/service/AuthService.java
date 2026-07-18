package kakaotech.task4.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.task4.common.exception.CommonFieldError;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.token.AuthTokenService;
import kakaotech.task4.common.security.token.dto.AccessTokenInfo;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.auth.code.AuthFieldError;
import kakaotech.task4.domain.auth.dto.req.SignInRequest;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
import kakaotech.task4.domain.auth.dto.res.SignInResponse;
import kakaotech.task4.domain.auth.dto.res.TokenReissueResponse;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    public void signUp(SignUpRequest request) {
        validatePasswordMatch(request);
        validateDuplicate(request);
        memberService.signUp(request);
    }

    private void validatePasswordMatch(SignUpRequest request) {
        if (!request.validatePasswordMatch()) {
            Map<String, Object> fieldErrors = new HashMap<>();
            fieldErrors.put(CommonFieldError.PASSWORD_MISMATCH.getField(), CommonFieldError.PASSWORD_MISMATCH.getMessage());
            throw new CustomException(AuthExceptionCode.VALIDATION_ERROR, fieldErrors);
        }
    }

    private void validateDuplicate(SignUpRequest request) {
        Map<String, Object> conflictErrors = new HashMap<>();

        if (memberService.existsByEmail(request.email())) {
            conflictErrors.put(AuthFieldError.DUPLICATE_EMAIL.getField(), AuthFieldError.DUPLICATE_EMAIL.getMessage());
        }

        if (memberService.existsByNickname(request.nickname())) {
            conflictErrors.put(CommonFieldError.DUPLICATE_NICKNAME.getField(), CommonFieldError.DUPLICATE_NICKNAME.getMessage());
        }

        if (!conflictErrors.isEmpty()) {
            throw new CustomException(AuthExceptionCode.CONFLICT, conflictErrors);
        }
    }

    public SignInResponse signIn(SignInRequest request, HttpServletResponse response) {
        Member member = getAuthenticatedUser(request);
        AccessTokenInfo accessTokenInfo = authTokenService.issue(member.getMemberUuid(), response);
        return SignInResponse.from(member.getProfileImageUrl(), accessTokenInfo.expiresAt());
    }

    public TokenReissueResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        AccessTokenInfo accessTokenInfo = authTokenService.reissue(request, response);
        return TokenReissueResponse.from(accessTokenInfo.expiresAt());
    }

    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        authTokenService.delete(request, response);
    }

    private Member getAuthenticatedUser(SignInRequest request) {
        Member member = memberService.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(AuthExceptionCode.INVALID_CREDENTIALS));
        validatePassword(member, request.password());
        return member;
    }

    private void validatePassword(Member member, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new CustomException(AuthExceptionCode.INVALID_CREDENTIALS);
        }
    }
}