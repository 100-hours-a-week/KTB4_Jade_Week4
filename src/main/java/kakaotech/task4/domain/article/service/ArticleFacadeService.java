package kakaotech.task4.domain.article.service;

import kakaotech.task4.domain.article.dto.cursor.ArticleCursor;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleDetailResponse;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.ArticleSummaryResponse;
import kakaotech.task4.domain.article.dto.res.CommentDetailResponse;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.service.ArticleLikeService;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import kakaotech.task4.domain.articleVote.service.ArticleVoteService;
import kakaotech.task4.domain.comment.service.ArticleCommentService;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class ArticleFacadeService {
    private final ArticleService articleService;
    private final ArticleCommentService articleCommentService;
    private final ArticleLikeService articleLikeService;
    private final ArticleVoteService articleVoteService;

    public CreateArticleResponse createArticle(Member member, CreateArticleRequest request) {
        String articleUuid = articleService.createArticle(member, request);
        return CreateArticleResponse.from(articleUuid);
    }

    public void updateArticle(Member member, String articleUuid, UpdateArticleRequest request) {
        articleService.updateArticle(member, articleUuid, request);
    }

    public void deleteArticle(Member member, String articleUuid) {
        articleService.deleteArticle(member, articleUuid);
    }

    @Transactional(readOnly = true)
    public ArticleListResponse getArticleList(Member member, String cursor, int size) {
        List<Article> articles = articleService.findArticlePage(cursor, size + 1);

        boolean hasNext = articles.size() > size;
        if (hasNext) articles = articles.subList(0, size);

        Map<Long, ArticleVoteCount> voteCounts = articleVoteService.findVoteCounts(articles);
        Map<Long, VoteOption> myVotes = articleVoteService.findMyVotes(member, articles);
        Set<Long> likedArticleIds = articleLikeService.findLikedArticleIds(member, articles);

        List<ArticleSummaryResponse> responses = articles.stream()
                .map(article -> ArticleSummaryResponse.of(
                        article,
                        voteCounts.get(article.getArticleId()),
                        myVotes.get(article.getArticleId()),
                        likedArticleIds.contains(article.getArticleId())))
                .toList();

        String nextCursor = null;
        if (hasNext) nextCursor = ArticleCursor.encode(articles.getLast());

        return ArticleListResponse.of(responses, hasNext, nextCursor);
    }

    @Transactional(readOnly = true)
    public ArticleDetailResponse getArticleDetail(Member member, String articleUuid) {
        Article article = articleService.findArticleByUuid(articleUuid);

        ArticleVoteCount voteCount = articleVoteService.findVoteCount(article);
        VoteOption myVote = articleVoteService.findMyVote(member, article);
        boolean isLiked = articleLikeService.isLiked(member, article);
        List<CommentDetailResponse> comments = articleCommentService.findCommentsByArticle(article);

        return ArticleDetailResponse.of(article, voteCount, myVote, isLiked, comments);
    }
}
