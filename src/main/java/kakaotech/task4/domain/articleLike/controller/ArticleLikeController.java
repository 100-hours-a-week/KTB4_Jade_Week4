package kakaotech.task4.domain.articleLike.controller;

import kakaotech.task4.common.resolver.CurrentUser;
import kakaotech.task4.domain.articleLike.api.ArticleLikeApi;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.service.ArticleLikeFacadeService;
import kakaotech.task4.domain.user.entity.User;
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
            @CurrentUser User user,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response = articleLikeFacadeService.like(user, articleUuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> unlike(
            @CurrentUser User user,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response = articleLikeFacadeService.unlike(user, articleUuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}