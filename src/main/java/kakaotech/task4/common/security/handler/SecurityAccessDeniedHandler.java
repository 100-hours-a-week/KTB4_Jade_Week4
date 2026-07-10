package kakaotech.task4.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import kakaotech.task4.common.response.ExceptionRes;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ExceptionCode exceptionCode = accessDeniedException instanceof CsrfException
                ? AuthExceptionCode.INVALID_CSRF_TOKEN
                : AuthExceptionCode.ACCESS_DENIED;

        response.setStatus(exceptionCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ExceptionRes.from(exceptionCode));
    }
}