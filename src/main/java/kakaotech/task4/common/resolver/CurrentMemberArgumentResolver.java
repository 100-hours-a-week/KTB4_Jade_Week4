package kakaotech.task4.common.resolver;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMember.class)
                && parameter.getParameterType().equals(AuthenticatedMember.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedMember principal)) {
            throw new CustomException(AuthExceptionCode.UNAUTHORIZED);
        }

        return principal;
    }
}
