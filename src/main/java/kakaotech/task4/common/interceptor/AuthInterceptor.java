package kakaotech.task4.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userUuid = request.getHeader("Authorization");
        validateAuthenticated(userUuid);
        return true;
    }

    private void validateAuthenticated(String userUuid) {
        if (userUuid == null || userUuid.isBlank()) {
            throw new CustomException(AuthExceptionCode.UNAUTHORIZED);
        }
        userService.findByUuid(userUuid)
                .orElseThrow(() -> new CustomException(AuthExceptionCode.UNAUTHORIZED));
    }
}