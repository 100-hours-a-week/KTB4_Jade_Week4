package kakaotech.task4.domain.articleVote.service;

import kakaotech.task4.domain.article.entity.Article;
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
        Optional<ArticleVote> found = articleVoteRepository.findByArticleAndMember(article, member);

        if (found.isEmpty()) {
            articleVoteRepository.save(ArticleVote.of(article, member, option));
            increaseCount(article.getArticleId(), option);
            return toResponse(article, option, true, true);
        }

        ArticleVote articleVote = found.get();
        if (!articleVote.changeOptionTo(option)) {
            return toResponse(article, option, false, false);
        }

        moveCount(article.getArticleId(), option);
        return toResponse(article, option, true, false);
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

    private void increaseCount(Long articleId, VoteOption option) {
        switch (option) {
            case A -> articleVoteCountRepository.increaseCountA(articleId);
            case B -> articleVoteCountRepository.increaseCountB(articleId);
        }
    }

    private void moveCount(Long articleId, VoteOption option) {
        switch (option) {
            case A -> articleVoteCountRepository.moveVoteFromBToA(articleId);
            case B -> articleVoteCountRepository.moveVoteFromAToB(articleId);
        }
    }

    private ArticleVoteResponse toResponse(Article article, VoteOption myVote, boolean changed, boolean wasFirst) {
        return ArticleVoteResponse.of(findVoteCount(article), myVote, changed, wasFirst);
    }
}
