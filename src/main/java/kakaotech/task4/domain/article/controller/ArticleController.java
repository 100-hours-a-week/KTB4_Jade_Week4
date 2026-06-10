package kakaotech.task4.domain.article.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentUser;
import kakaotech.task4.domain.article.api.ArticleApi;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleDetailResponse;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.service.ArticleFacadeService;
import kakaotech.task4.domain.user.entity.User;
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
    public ResponseEntity<?> createArticle(@CurrentUser User user,
                                           @Valid @RequestBody CreateArticleRequest request) {
        CreateArticleResponse response = articleFacadeService.createArticle(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> updateArticle(
            @CurrentUser User user,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody UpdateArticleRequest request) {
        articleFacadeService.updateArticle(user, articleUuid, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> deleteArticle(
            @CurrentUser User user,
            @PathVariable("article-uuid") String articleUuid) {
        articleFacadeService.deleteArticle(user, articleUuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/list")
    @Override
    public ResponseEntity<?> getArticleList(
            @CurrentUser User user,
            @RequestParam(required = false) String lastArticleUuid,
            @RequestParam(defaultValue = "10") int size) {
        ArticleListResponse response = articleFacadeService.getArticleList(lastArticleUuid, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<?> getArticleDetail(
            @CurrentUser User user,
            @PathVariable("uuid") String articleUuid) {
        ArticleDetailResponse response = articleFacadeService.getArticleDetail(user, articleUuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
