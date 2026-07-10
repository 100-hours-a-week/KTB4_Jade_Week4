package kakaotech.task4.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.response.SuccessRes;
import kakaotech.task4.domain.auth.api.AuthApi;
import kakaotech.task4.domain.auth.code.AuthSuccessCode;
import kakaotech.task4.domain.auth.dto.req.SignInRequest;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
import kakaotech.task4.domain.auth.dto.res.SignInResponse;
import kakaotech.task4.domain.auth.dto.res.TokenReissueResponse;
import kakaotech.task4.domain.auth.service.AuthService;
import kakaotech.task4.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/sign-in")
    @Override
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request, HttpServletResponse response) {
        SignInResponse signInResponse = authService.signIn(request, response);
        return ResponseEntity.status(HttpStatus.OK).body(signInResponse);
    }

    @PostMapping("/sign-out")
    @Override
    public ResponseEntity<?> signOut(@CurrentMember Member member, HttpServletRequest request, HttpServletResponse response) {
        authService.signOut(request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/token/re-issue")
    @Override
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenReissueResponse tokenReissueResponse = authService.reissue(request, response);
        return ResponseEntity.status(HttpStatus.OK).body(tokenReissueResponse);
    }

    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf(CsrfToken csrfToken) {
        csrfToken.getToken();
        return ResponseEntity.noContent().build();
    }
}