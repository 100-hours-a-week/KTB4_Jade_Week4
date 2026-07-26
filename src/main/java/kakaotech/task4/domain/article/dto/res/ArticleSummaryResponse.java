package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import kakaotech.task4.domain.member.entity.Member;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ArticleSummaryResponse(
        String articleUuid,
        String title,
        String optionA,
        String optionB,
        int voteCountA,
        int voteCountB,
        VoteOption myVote,
        String writer,
        boolean isMine,
        String profileImageUrl,
        Instant createdAt,
        int likeCount,
        boolean isLiked
) {
    public static ArticleSummaryResponse of(Article article,
                                            Member viewer,
                                            ArticleVoteCount voteCount,
                                            VoteOption myVote,
                                            boolean isLiked) {
        return ArticleSummaryResponse.builder()
                .articleUuid(article.getArticleUuid())
                .title(article.getTitle())
                .optionA(article.getOptionA())
                .optionB(article.getOptionB())
                .voteCountA(voteCount.getCountA())
                .voteCountB(voteCount.getCountB())
                .myVote(myVote)
                .writer(article.getMember().getNickname())
                .isMine(article.getMember().equals(viewer))
                .profileImageUrl(article.getMember().getProfileImageUrl())
                .createdAt(article.getCreatedAt())
                .likeCount(article.getLikedCount())
                .isLiked(isLiked)
                .build();
    }
}
