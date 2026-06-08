package kakaotech.task4.domain.comment.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentExceptionCode implements ExceptionCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "ARTICLE-400-001", "필수 값이 제외되었습니다."),
    FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN, "COMMENT-403-001", "수정 권한이 없습니다."),
    FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "COMMENT-403-002", "삭제 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-404-001", "해당 댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}