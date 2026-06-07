package kakaotech.task4.domain.auth.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.response.SuccessRes;
import kakaotech.task4.domain.auth.api.AuthApi;
import kakaotech.task4.domain.auth.code.AuthSuccessCode;
import kakaotech.task4.domain.auth.dto.SignUpRequest;
import kakaotech.task4.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthApi {
    private final AuthService authService;

    @PostMapping("/sign-up")
    @Override
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        SuccessRes body = SuccessRes.from(AuthSuccessCode.SIGN_UP_SUCCESS.getStatus(), AuthSuccessCode.SIGN_UP_SUCCESS.getMessage());
        return ResponseEntity.status(AuthSuccessCode.SIGN_UP_SUCCESS.getStatus()).body(body);
    }
}
