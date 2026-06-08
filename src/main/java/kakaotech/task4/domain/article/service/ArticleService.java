package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.code.ArticleExceptionCode;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
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

    public Article createArticle(String userUuid, CreateArticleRequest request) {
        User user = findUserByUuid(userUuid);
        String articleUuid = UuidCreator.create(UuidPrefix.ARTICLE);
        Article article = Article.of(articleUuid, user, request);
        articleRepository.save(article);
        return article;
    }

    public Article updateArticle(String userUuid, String articleUuid, UpdateArticleRequest request) {
        User user = findUserByUuid(userUuid);
        Article article = findArticleByUuid(articleUuid);
        validateOwner(user, article, ArticleExceptionCode.FORBIDDEN_UPDATE);
        article.update(request);
        return article;
    }

    public void deleteArticle(String userUuid, String articleUuid) {
        User user = findUserByUuid(userUuid);
        Article article = findArticleByUuid(articleUuid);
        validateOwner(user, article, ArticleExceptionCode.FORBIDDEN_DELETE);
        article.softDelete();
    }

    private User findUserByUuid(String userUuid) {
        return userService.findByUuid(userUuid)
                .orElseThrow(() -> new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR));
    }

    public Article findArticleByUuid(String articleUuid) {
        return articleRepository.findByUuid(articleUuid)
                .orElseThrow(() -> new CustomException(ArticleExceptionCode.NOT_FOUND));
    }

    private void validateOwner(User user, Article article, ArticleExceptionCode exceptionCode) {
        if (!article.getUser().equals(user)) {
            throw new CustomException(exceptionCode);
        }
    }
}