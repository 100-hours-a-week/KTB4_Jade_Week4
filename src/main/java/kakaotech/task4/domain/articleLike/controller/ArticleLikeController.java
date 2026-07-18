package kakaotech.task4.domain.articleLike.controller;

import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.articleLike.api.ArticleLikeApi;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.service.ArticleLikeFacadeService;
import kakaotech.task4.common.response.ApiResponse;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
            @CurrentMember Member member,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response = articleLikeFacadeService.like(member, articleUuid);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> unlike(
            @CurrentMember Member member,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response = articleLikeFacadeService.unlike(member, articleUuid);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}