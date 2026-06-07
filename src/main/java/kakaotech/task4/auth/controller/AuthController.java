package kakaotech.task4.auth.controller;

import jakarta.validation.Valid;
import kakaotech.task4.auth.api.AuthApi;
import kakaotech.task4.auth.dto.SignUpRequest;
import kakaotech.task4.auth.service.AuthService;
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

        return null;
    }
}
