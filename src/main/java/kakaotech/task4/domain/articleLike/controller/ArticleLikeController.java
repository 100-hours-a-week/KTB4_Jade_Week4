package kakaotech.task4.domain.articleLike.controller;

import kakaotech.task4.domain.articleLike.api.ArticleLikeApi;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.articleLike.service.ArticleLikeFacadeService;
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
            @RequestHeader("Authorization") String userUuid,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response = articleLikeFacadeService.like(userUuid, articleUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> unlike(
            @RequestHeader("Authorization") String userUuid,
            @PathVariable("article-uuid") String articleUuid) {
        ArticleLikeResponse response = articleLikeFacadeService.unlike(userUuid, articleUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}