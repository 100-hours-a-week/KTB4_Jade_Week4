package kakaotech.task4.domain.article.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ArticleSuccessCode {
    CREATE_ARTICLE_SUCCESS(HttpStatus.CREATED, "게시글이 작성되었습니다.");

    private final HttpStatus status;
    private final String message;
}