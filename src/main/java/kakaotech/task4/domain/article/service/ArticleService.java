package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.code.ArticleExceptionCode;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserService userService;

    public CreateArticleResponse createArticle(String userUuid, CreateArticleRequest request) {
        User user = validateAuthenticated(userUuid);

        String articleUuid = UuidCreator.create(UuidPrefix.ARTICLE);

        Article article = Article.of(articleUuid, user, request);
        articleRepository.save(article);
        return CreateArticleResponse.from(articleUuid);
    }

    private User validateAuthenticated(String userUuid) {
        return userService.findByUuid(userUuid)
                .orElseThrow(() -> new CustomException(ArticleExceptionCode.UNAUTHORIZED));
    }

}
