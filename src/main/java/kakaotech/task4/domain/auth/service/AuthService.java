package kakaotech.task4.domain.auth.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.auth.code.AuthFieldError;
import kakaotech.task4.domain.auth.dto.req.SignInRequest;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
import kakaotech.task4.domain.auth.dto.res.SignInResponse;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserService userService;

    public void signUp(SignUpRequest request) {
        validatePasswordMatch(request);
        validateDuplicate(request);
        userService.signUp(request);
    }

    private void validatePasswordMatch(SignUpRequest request) {
        if (!request.validatePasswordMatch()) {
            Map<String, Object> fieldErrors = new HashMap<>();
            fieldErrors.put(AuthFieldError.PASSWORD_MISMATCH.getField(), AuthFieldError.PASSWORD_MISMATCH.getMessage());
            throw new CustomException(AuthExceptionCode.VALIDATION_ERROR, fieldErrors);
        }
    }

    private void validateDuplicate(SignUpRequest request) {
        Map<String, Object> conflictErrors = new HashMap<>();

        if (userService.existsByEmail(request.email())) {
            conflictErrors.put(AuthFieldError.DUPLICATE_EMAIL.getField(), AuthFieldError.DUPLICATE_EMAIL.getMessage());
        }

        if (userService.existsByNickname(request.nickname())) {
            conflictErrors.put(AuthFieldError.DUPLICATE_NICKNAME.getField(), AuthFieldError.DUPLICATE_NICKNAME.getMessage());
        }

        if (!conflictErrors.isEmpty()) {
            throw new CustomException(AuthExceptionCode.CONFLICT, conflictErrors);
        }
    }

    public SignInResponse signIn(SignInRequest request) {
        User user = getAuthenticatedUser(request);
        return SignInResponse.from(user.getProfileImageUrl());
    }

    private User getAuthenticatedUser(SignInRequest request) {
        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(AuthExceptionCode.INVALID_CREDENTIALS));
        validatePassword(user, request.password());
        return user;
    }

    private void validatePassword(User user, String password) {
        if (!user.getPassword().equals(password)) {
            throw new CustomException(AuthExceptionCode.INVALID_CREDENTIALS);
        }
    }

    //todo : 추후 jwt, 세션 등으로 상태 관리 할 시 비즈니스 로직 추가.
    public void signOut() {

    }
}
