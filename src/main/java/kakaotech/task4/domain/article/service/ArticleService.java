package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.code.ArticleExceptionCode;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.ArticleSummaryResponse;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserService userService;

    public Article createArticle(String userUuid, CreateArticleRequest request) {
        User user = userService.findByUuid(userUuid, GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        String articleUuid = UuidCreator.create(UuidPrefix.ARTICLE);
        Article article = Article.of(articleUuid, user, request);
        articleRepository.save(article);
        return article;
    }

    public Article updateArticle(String userUuid, String articleUuid, UpdateArticleRequest request) {
        User user = userService.findByUuid(userUuid, GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        Article article = findArticleByUuid(articleUuid);
        validateOwner(user, article, ArticleExceptionCode.FORBIDDEN_UPDATE);
        article.update(request);
        return article;
    }

    public void deleteArticle(String userUuid, String articleUuid) {
        User user = userService.findByUuid(userUuid, GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        Article article = findArticleByUuid(articleUuid);
        validateOwner(user, article, ArticleExceptionCode.FORBIDDEN_DELETE);
        article.softDelete();
    }

    public Article findArticleByUuid(String articleUuid) {
        return articleRepository.findByUuid(articleUuid)
                .orElseThrow(() -> new CustomException(ArticleExceptionCode.NOT_FOUND));
    }

    public ArticleListResponse getArticleList(String lastArticleUuid, int size) {
        List<Article> articles = articleRepository.findByCursor(lastArticleUuid, size + 1);

        boolean hasNext = articles.size() > size;
        if (hasNext) articles = articles.subList(0, size);

        List<ArticleSummaryResponse> responses = articles.stream()
                .map(ArticleSummaryResponse::from)
                .collect(Collectors.toList());

        String nextCursor = hasNext ? articles.getLast().getArticleUuid() : null;
        return ArticleListResponse.of(responses, hasNext, nextCursor);
    }

    private void validateOwner(User user, Article article, ArticleExceptionCode exceptionCode) {
        if (!article.getUser().equals(user)) {
            throw new CustomException(exceptionCode);
        }
    }
}