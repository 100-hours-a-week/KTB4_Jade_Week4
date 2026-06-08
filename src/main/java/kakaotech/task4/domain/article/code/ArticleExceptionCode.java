package kakaotech.task4.domain.article.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ArticleExceptionCode implements ExceptionCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ARTICLE-401-001", "로그인 후 사용할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}