package kakaotech.task4.domain.article.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ArticleExceptionCode implements ExceptionCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "ARTICLE-400-001", "변경할 내용을 입력해주세요"),
    FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN, "ARTICLE-403-001", "수정 권한이 없습니다."),
    FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "ARTICLE-403-002", "삭제 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE-404-001", "해당 게시글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}