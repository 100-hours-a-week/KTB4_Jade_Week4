package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.code.ArticleExceptionCode;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.ArticleSummaryResponse;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional
    public Article createArticle(Member member, CreateArticleRequest request) {
        String articleUuid = UuidCreator.create(UuidPrefix.ARTICLE);
        Article article = Article.of(articleUuid, member, request);
        articleRepository.save(article);
        return article;
    }

    @Transactional
    public Article updateArticle(Member member, String articleUuid, UpdateArticleRequest request) {
        Article article = findArticleByUuid(articleUuid);
        validateOwner(member, article, ArticleExceptionCode.FORBIDDEN_UPDATE);
        validateAllNull(request);

        article.update(request);
        return article;
    }

    @Transactional
    public void deleteArticle(Member member, String articleUuid) {
        Article article = findArticleByUuid(articleUuid);
        validateOwner(member, article, ArticleExceptionCode.FORBIDDEN_DELETE);
        article.softDelete();
    }

    public Article findArticleByUuid(String articleUuid) {
        return articleRepository.findByArticleUuid(articleUuid)
                .orElseThrow(() -> new CustomException(ArticleExceptionCode.NOT_FOUND));
    }

    public ArticleListResponse getArticleList(String lastArticleUuid, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Article> articles;
        if (lastArticleUuid == null) {
            articles = articleRepository.findFirstPage(pageable);
        } else {
            Article cursor = findArticleByUuid(lastArticleUuid);
            articles = articleRepository.findNextPage(cursor.getCreatedAt(), pageable);
        }

        boolean hasNext = articles.size() > size;
        if (hasNext) articles = articles.subList(0, size);

        List<ArticleSummaryResponse> responses = articles.stream()
                .map(ArticleSummaryResponse::from)
                .collect(Collectors.toList());

        String nextCursor = hasNext ? articles.getLast().getArticleUuid() : null;
        return ArticleListResponse.of(responses, hasNext, nextCursor);
    }

    private void validateOwner(Member member, Article article, ArticleExceptionCode exceptionCode) {
        if (!article.getMember().equals(member)) {
            throw new CustomException(exceptionCode);
        }
    }

    private void validateAllNull(UpdateArticleRequest request) {
        if (request.isAllNull()) {
            throw new CustomException(ArticleExceptionCode.BAD_REQUEST);
        }
    }
}