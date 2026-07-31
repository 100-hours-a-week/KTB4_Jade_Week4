package kakaotech.task4.domain.articleVote.code;

import kakaotech.task4.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ArticleVoteExceptionCode implements ExceptionCode {
    VOTE_COUNT_NOT_FOUND(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ARTICLE-VOTE-500-001",
            "투표 집계 정보를 찾을 수 없습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
