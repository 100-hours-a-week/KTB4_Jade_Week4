package kakaotech.task4.domain.auth.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessCode {
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다.");

    private final HttpStatus status;
    private final String message;
}
