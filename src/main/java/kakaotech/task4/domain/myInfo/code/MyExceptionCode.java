package kakaotech.task4.domain.myInfo.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyExceptionCode implements ExceptionCode {
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MY-409-001", "중복된 닉네임입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}