package kakaotech.task4.articleLike;

import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.exception.GlobalExceptionHandler;
import kakaotech.task4.common.response.ExceptionRes;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("DataIntegrityViolationException이 발생하면 DATA_INTEGRITY_VIOLATION 응답으로 변환한다")
    void handleDataIntegrityViolationException() {
        // given
        ConstraintViolationException cause = new ConstraintViolationException(
                "unique constraint violation",
                new SQLException("unique constraint violation"),
                "uk_article_like_article_member"
        );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("데이터 무결성 제약 조건 위반", cause);

        // when
        ResponseEntity<?> response =
                globalExceptionHandler.handleDataIntegrityViolationException(exception);

        // then
        assertThat(response.getStatusCode())
                .isEqualTo(GlobalExceptionCode.DATA_INTEGRITY_VIOLATION.getStatus());

        assertThat(response.getBody())
                .isInstanceOf(ExceptionRes.class);

        ExceptionRes body = (ExceptionRes) response.getBody();

        assertThat(body.code())
                .isEqualTo(GlobalExceptionCode.DATA_INTEGRITY_VIOLATION.getCode());

        assertThat(body.status())
                .isEqualTo(GlobalExceptionCode.DATA_INTEGRITY_VIOLATION.getStatus());

        assertThat(body.message())
                .isEqualTo(GlobalExceptionCode.DATA_INTEGRITY_VIOLATION.getMessage());
    }
}