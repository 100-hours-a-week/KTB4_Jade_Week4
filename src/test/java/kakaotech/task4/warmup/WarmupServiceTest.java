package kakaotech.task4.warmup;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.security.properties.CookieProperties;
import kakaotech.task4.common.security.token.AccessTokenProvider;
import kakaotech.task4.common.warmup.WarmupProperties;
import kakaotech.task4.common.warmup.WarmupService;
import kakaotech.task4.common.warmup.code.WarmupExceptionCode;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class WarmupServiceTest {

    private final CookieProperties cookieProperties = mock(CookieProperties.class);
    private final AccessTokenProvider accessTokenProvider = mock(AccessTokenProvider.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ArticleRepository articleRepository = mock(ArticleRepository.class);

    @Test
    void 시크릿이_일치하지_않으면_워밍업을_실행하지_않는다() {
        WarmupProperties properties = new WarmupProperties(
                "warmup-secret", 1000, 10, "http://127.0.0.1:8080/api", List.of("/articles"));
        WarmupService service = new WarmupService(
                properties, cookieProperties, accessTokenProvider, memberRepository, articleRepository);

        assertThatThrownBy(() -> service.warmUp("invalid-secret", null))
                .isInstanceOfSatisfying(CustomException.class,
                        exception -> assertThat(exception.getExceptionCode()).isEqualTo(WarmupExceptionCode.NOT_FOUND));
        verifyNoInteractions(cookieProperties, accessTokenProvider, memberRepository, articleRepository);
    }

    @Test
    void 설정된_시크릿이_비어_있으면_워밍업을_실행하지_않는다() {
        WarmupProperties properties = new WarmupProperties(
                " ", 1000, 10, "http://127.0.0.1:8080/api", List.of("/articles"));
        WarmupService service = new WarmupService(
                properties, cookieProperties, accessTokenProvider, memberRepository, articleRepository);

        assertThatThrownBy(() -> service.warmUp(null, null))
                .isInstanceOfSatisfying(CustomException.class,
                        exception -> assertThat(exception.getExceptionCode()).isEqualTo(WarmupExceptionCode.NOT_FOUND));
        verifyNoInteractions(cookieProperties, accessTokenProvider, memberRepository, articleRepository);
    }
}
