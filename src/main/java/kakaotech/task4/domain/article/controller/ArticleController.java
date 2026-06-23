package kakaotech.task4.domain.article.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.article.api.ArticleApi;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleDetailResponse;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.service.ArticleFacadeService;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController implements ArticleApi {
    private final ArticleFacadeService articleFacadeService;

    @PostMapping
    @Override
    public ResponseEntity<?> createArticle(@CurrentMember Member member,
                                           @Valid @RequestBody CreateArticleRequest request) {
        CreateArticleResponse response = articleFacadeService.createArticle(member, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> updateArticle(
            @CurrentMember Member member,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody UpdateArticleRequest request) {
        articleFacadeService.updateArticle(member, articleUuid, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> deleteArticle(
            @CurrentMember Member member,
            @PathVariable("article-uuid") String articleUuid) {
        articleFacadeService.deleteArticle(member, articleUuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getArticleList(
            @CurrentMember Member member,
            @RequestParam(required = false) String lastArticleUuid,
            @RequestParam(defaultValue = "10") int size) {
        ArticleListResponse response = articleFacadeService.getArticleList(lastArticleUuid, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<?> getArticleDetail(
            @CurrentMember Member member,
            @PathVariable("uuid") String articleUuid) {
        ArticleDetailResponse response = articleFacadeService.getArticleDetail(member, articleUuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
