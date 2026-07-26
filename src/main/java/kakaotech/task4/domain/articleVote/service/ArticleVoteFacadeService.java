package kakaotech.task4.domain.articleVote.service;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.articleVote.dto.req.CreateVoteRequest;
import kakaotech.task4.domain.articleVote.dto.res.ArticleVoteResponse;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class ArticleVoteFacadeService {
    private final ArticleService articleService;
    private final ArticleVoteService articleVoteService;

    @Transactional
    public ArticleVoteResponse vote(Member member, String articleUuid, CreateVoteRequest request) {
        Article article = articleService.findArticleByUuid(articleUuid);
        return articleVoteService.vote(member, article, request.option());
    }
}
