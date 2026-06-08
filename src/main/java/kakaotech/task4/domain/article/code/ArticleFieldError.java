package kakaotech.task4.domain.article.code;


import kakaotech.task4.common.exception.FieldError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleFieldError implements FieldError {
    TITLE_REQUIRED("title", "제목을 입력해주세요."),
    CONTENT_REQUIRED("content", "내용을 입력해주세요."),
    TITLE_MAX_LENGTH("title", "제목의 최대 길이는 26자 입니다.");

    private final String field;
    private final String message;
}