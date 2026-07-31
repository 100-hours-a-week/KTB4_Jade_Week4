package kakaotech.task4.domain.articleVote.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleVote.code.ArticleVoteExceptionCode;
import kakaotech.task4.domain.articleVote.dto.res.ArticleVoteResponse;
import kakaotech.task4.domain.articleVote.entity.ArticleVote;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteCountRepository;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteRepository;
import kakaotech.task4.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleVoteService {
    private final ArticleVoteRepository articleVoteRepository;
    private final ArticleVoteCountRepository articleVoteCountRepository;

    @Transactional
    public ArticleVoteResponse vote(Member member, Article article, VoteOption option) {
        Long articleId = article.getArticleId();
        ArticleVoteCount voteCount = articleVoteCountRepository
                .findByIdForUpdate(articleId)
                .orElseThrow(() -> new CustomException(ArticleVoteExceptionCode.VOTE_COUNT_NOT_FOUND));

        Optional<ArticleVote> found = articleVoteRepository.findByArticleAndMember(article, member);

        if (found.isEmpty()) {
            articleVoteRepository.save(ArticleVote.of(article, member, option));
            voteCount.increase(option);
            return ArticleVoteResponse.of(voteCount, option, true, true);
        }

        ArticleVote articleVote = found.get();
        if (!articleVote.changeOptionTo(option)) {
            return ArticleVoteResponse.of(voteCount, option, false, false);
        }

        voteCount.moveTo(option);
        return ArticleVoteResponse.of(voteCount, option, true, false);
    }

    public ArticleVoteCount findVoteCount(Article article) {
        return articleVoteCountRepository.findById(article.getArticleId())
                .orElseGet(() -> ArticleVoteCount.of(article.getArticleId()));
    }

    public VoteOption findMyVote(Member member, Article article) {
        return articleVoteRepository.findByArticleAndMember(article, member)
                .map(ArticleVote::getVoteOption)
                .orElse(null);
    }

    public Map<Long, ArticleVoteCount> findVoteCounts(List<Article> articles) {
        List<Long> articleIds = articles.stream()
                .map(Article::getArticleId)
                .toList();

        Map<Long, ArticleVoteCount> voteCounts = new HashMap<>();
        articleVoteCountRepository.findAllById(articleIds)
                .forEach(voteCount -> voteCounts.put(voteCount.getArticleId(), voteCount));

        articles.forEach(article -> voteCounts.putIfAbsent(article.getArticleId(), ArticleVoteCount.of(article.getArticleId())));
        return voteCounts;
    }

    public Map<Long, VoteOption> findMyVotes(Member member, List<Article> articles) {
        Map<Long, VoteOption> myVotes = new HashMap<>();
        articleVoteRepository.findAllByMemberAndArticleIn(member, articles)
                .forEach(vote -> myVotes.put(vote.getArticle().getArticleId(), vote.getVoteOption()));

        return myVotes;
    }

}
