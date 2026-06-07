package kakaotech.task4.domain.auth.service;

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
        validateRequest(request);
        userService.signUp(request);
    }

    private void validateRequest(SignUpRequest request) {
        Map<String, String> fieldErrors = n가ew HashMap<>();

        if(!request.validatePasswordMatch()) {
            fieldErrors.put()
        }

    }

}
