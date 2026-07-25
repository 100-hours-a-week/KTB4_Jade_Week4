package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.code.ArticleExceptionCode;
import kakaotech.task4.domain.article.dto.cursor.ArticleCursor;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteCountRepository;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleVoteCountRepository articleVoteCountRepository;

    @Transactional
    public String createArticle(Member member, CreateArticleRequest request) {
        String articleUuid = UuidCreator.create(UuidPrefix.ARTICLE);
        Article article = Article.of(articleUuid, member, request);

        articleRepository.save(article);
        articleVoteCountRepository.save(ArticleVoteCount.of(article.getArticleId()));
        return article.getArticleUuid();
    }

    @Transactional
    public void updateArticle(Member member, String articleUuid, UpdateArticleRequest request) {
        Article article = findArticleByUuid(articleUuid);
        validateOwner(member, article, ArticleExceptionCode.FORBIDDEN_UPDATE);
        validateAllNull(request);

        article.update(request);
    }

    @Transactional
    public void deleteArticle(Member member, String articleUuid) {
        Article article = findArticleByUuid(articleUuid);
        validateOwner(member, article, ArticleExceptionCode.FORBIDDEN_DELETE);
        article.softDelete();
    }

    @Transactional
    public int increaseLikedCount(Long articleId) {
        return articleRepository.increaseLikedCount(articleId);
    }

    @Transactional
    public int decreaseLikedCount(Long articleId) {
       return articleRepository.decreaseLikedCount(articleId);
    }

    public Article findArticleByUuid(String articleUuid) {
        return articleRepository.findByArticleUuid(articleUuid)
                .orElseThrow(() -> new CustomException(ArticleExceptionCode.NOT_FOUND));
    }

    public List<Article> findArticlePage(String cursor, int limit) {
        Pageable pageable = PageRequest.ofSize(limit);

        if (cursor == null) {
            return articleRepository.findFirstPage(pageable);
        }
        return findNextPage(cursor, pageable);
    }

    private List<Article> findNextPage(String cursor, Pageable pageable) {
        ArticleCursor c = ArticleCursor.decode(cursor);
        return articleRepository.findNextPage(c.createdAt(), c.articleId(), pageable);
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