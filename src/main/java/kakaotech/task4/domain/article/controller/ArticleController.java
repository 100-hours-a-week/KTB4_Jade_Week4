package kakaotech.task4.domain.article.controller;

import jakarta.validation.Valid;
import kakaotech.task4.domain.article.api.ArticleApi;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.service.ArticleFacadeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController implements ArticleApi {
    private final ArticleFacadeService articleFacadeService;

    @PostMapping
    @Override
    public ResponseEntity<?> createArticle(@RequestHeader("Authorization") String userUuid,
                                           @Valid @RequestBody CreateArticleRequest request) {
        CreateArticleResponse response = articleFacadeService.createArticle(userUuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> updateArticle(
            @RequestHeader("Authorization") String userUuid,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody UpdateArticleRequest request) {
        articleFacadeService.updateArticle(userUuid, articleUuid, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> deleteArticle(
            @RequestHeader("Authorization") String userUuid,
            @PathVariable("article-uuid") String articleUuid) {
        articleFacadeService.deleteArticle(userUuid, articleUuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
