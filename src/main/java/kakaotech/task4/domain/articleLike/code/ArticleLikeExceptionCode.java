package kakaotech.task4.domain.articleLike.code;

import kakaotech.task4.common.exception.ExceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ArticleLikeExceptionCode implements ExceptionCode {
    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ARTICLE_LIKE-400-001", "좋아요를 누르지 않은 게시글입니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "ARTICLE_LIKE-400-002", "이미 좋아요를 누른 게시글입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}