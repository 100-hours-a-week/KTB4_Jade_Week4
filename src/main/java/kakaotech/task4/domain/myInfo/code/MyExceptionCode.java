package kakaotech.task4.domain.myInfo.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyExceptionCode implements ExceptionCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "MY-400-001", "변경할 내용을 입력해주세요."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "MY-404-001", "이미 탈퇴한 회원입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MY-409-001", "중복된 닉네임입니다."),
    INVALID_PASSWORD(HttpStatus.UNPROCESSABLE_ENTITY, "MY-422-001", "필드의 유효성 검사가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}