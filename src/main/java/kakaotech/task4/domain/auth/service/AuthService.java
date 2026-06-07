package kakaotech.task4.domain.auth.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.auth.dto.SignUpRequest;
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
            fieldErrors.put("checkPassword", AuthExceptionCode.PASSWORD_MISMATCH.getMessage());
            throw new CustomException(AuthExceptionCode.VALIDATION_ERROR, fieldErrors);
        }
    }

    private void validateDuplicate(SignUpRequest request) {
        Map<String, Object> conflictErrors = new HashMap<>();

        if (userService.existsByEmail(request.email())) {
            conflictErrors.put("email", AuthExceptionCode.DUPLICATE_EMAIL.getMessage());
        }

        if (userService.existsByNickname(request.nickname())) {
            conflictErrors.put("nickname", AuthExceptionCode.DUPLICATE_NICKNAME.getMessage());
        }

        if (!conflictErrors.isEmpty()) {
            throw new CustomException(AuthExceptionCode.CONFLICT, conflictErrors);
        }
    }

}
