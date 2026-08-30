package kakaotech.task4.domain.articleVote.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.response.ApiResponse;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.articleVote.api.ArticleVoteApi;
import kakaotech.task4.domain.articleVote.dto.req.CreateVoteRequest;
import kakaotech.task4.domain.articleVote.dto.res.ArticleVoteResponse;
import kakaotech.task4.domain.articleVote.service.ArticleVoteFacadeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles/{article-uuid}/vote")
@AllArgsConstructor
public class ArticleVoteController implements ArticleVoteApi {
    private final ArticleVoteFacadeService articleVoteFacadeService;

    @PostMapping
    @Override
    public ResponseEntity<?> vote(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody CreateVoteRequest request) {
        ArticleVoteResponse response =
                articleVoteFacadeService.vote(member.memberUuid(), articleUuid, request);
        return ApiResponse.success(response).toEntity();
    }
}
