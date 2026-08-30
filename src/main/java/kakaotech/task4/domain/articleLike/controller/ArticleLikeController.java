package kakaotech.task4.domain.articleLike.controller;

import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.articleLike.api.ArticleLikeApi;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.service.ArticleLikeFacadeService;
import kakaotech.task4.common.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles/{article-uuid}/like")
@AllArgsConstructor
public class ArticleLikeController implements ArticleLikeApi {
    private final ArticleLikeFacadeService articleLikeFacadeService;

    @PostMapping
    @Override
    public ResponseEntity<?> like(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response =
                articleLikeFacadeService.like(member.memberUuid(), articleUuid);
        return ApiResponse.success(response).toEntity();
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> unlike(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response =
                articleLikeFacadeService.unlike(member.memberUuid(), articleUuid);
        return ApiResponse.success(response).toEntity();
    }
}
