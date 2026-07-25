package kakaotech.task4.domain.article.dto.res;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.entity.VoteOption;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ArticleDetailResponse(
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
        boolean isLiked,
        List<CommentDetailResponse> comments
) {
    public static ArticleDetailResponse of(Article article,
                                           ArticleVoteCount voteCount,
                                           VoteOption myVote,
                                           boolean isLiked,
                                           List<CommentDetailResponse> comments) {
        return ArticleDetailResponse.builder()
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
                .comments(comments)
                .build();
    }
}
