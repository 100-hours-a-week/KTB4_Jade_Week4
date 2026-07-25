package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import lombok.Builder;

import java.time.LocalDateTime;

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
        String userUuid,
        String profileImageUrl,
        LocalDateTime createdAt,
        int likeCount,
        boolean isLiked
) {
    public static ArticleSummaryResponse of(Article article,
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
                .userUuid(article.getMember().getMemberUuid())
                .profileImageUrl(article.getMember().getProfileImageUrl())
                .createdAt(article.getCreatedAt())
                .likeCount(article.getLikedCount())
                .isLiked(isLiked)
                .build();
    }
}
