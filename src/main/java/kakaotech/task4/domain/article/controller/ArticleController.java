package kakaotech.task4.domain.article.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.article.api.ArticleApi;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.article.dto.res.ArticleDetailResponse;
import kakaotech.task4.domain.article.dto.res.ArticleListResponse;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.service.ArticleFacadeService;
import kakaotech.task4.common.response.ApiResponse;
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
    public ResponseEntity<?> createArticle(@CurrentMember AuthenticatedMember member,
                                           @Valid @RequestBody CreateArticleRequest request) {
        CreateArticleResponse response =
                articleFacadeService.createArticle(member.memberUuid(), request);
        return ApiResponse.created(response).toEntity();
    }

    @PatchMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> updateArticle(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody UpdateArticleRequest request) {
        articleFacadeService.updateArticle(member.memberUuid(), articleUuid, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{article-uuid}")
    @Override
    public ResponseEntity<?> deleteArticle(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid) {
        articleFacadeService.deleteArticle(member.memberUuid(), articleUuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getArticleList(
            @CurrentMember AuthenticatedMember member,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "조회 개수는 최소 1개입니다.")
            @Max(value = 10, message = "조회 개수는 최대 10개입니다.")
            int size) {
        ArticleListResponse response =
                articleFacadeService.getArticleList(member.memberUuid(), cursor, size);
        return ApiResponse.success(response).toEntity();
    }

    @GetMapping("/{uuid}")
    @Override
    public ResponseEntity<?> getArticleDetail(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("uuid") String articleUuid) {
        ArticleDetailResponse response =
                articleFacadeService.getArticleDetail(member.memberUuid(), articleUuid);
        return ApiResponse.success(response).toEntity();
    }
}
