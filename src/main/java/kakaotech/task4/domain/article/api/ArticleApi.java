package kakaotech.task4.domain.article.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentUser;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import kakaotech.task4.domain.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[게시물 API]", description = "게시물 관련 API")
public interface ArticleApi {

    @Operation(summary = "게시글 작성", description = "게시글 작성 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerSuccessExamples.CREATE_ARTICLE_201))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_400_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_422_001)))
    })
    ResponseEntity<?> createArticle(
            @Parameter(description = "유저 UUID", required = true) @CurrentUser User user,
            @Valid @RequestBody CreateArticleRequest request);

    @Operation(summary = "게시글 수정", description = "게시글 수정 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 수정 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "변경할 내용 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_400_002))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_403_001))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_422_001)))
    })
    ResponseEntity<?> updateArticle(
            @Parameter(description = "유저 UUID", required = true)  @CurrentUser User user,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("uuid") String articleUuid,
            @Valid @RequestBody UpdateArticleRequest request);

    @Operation(summary = "게시글 삭제", description = "게시글 삭제 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_403_002))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001)))
    })
    ResponseEntity<?> deleteArticle(
            @Parameter(description = "유저 UUID", required = true) @CurrentUser User user,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid);

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록 조회 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerSuccessExamples.ARTICLE_200_001)))
    })
    ResponseEntity<?> getArticleList(
            @Parameter(description = "유저 UUID", required = true) @CurrentUser User user,
            @Parameter(description = "마지막 게시글 UUID") @RequestParam(required = false) String lastArticleUuid,
            @Parameter(description = "조회 수", example = "10") @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 조회 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerSuccessExamples.ARTICLE_200_002))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001)))
    })
    ResponseEntity<?> getArticleDetail(
            @Parameter(description = "유저 UUID", required = true) @CurrentUser User user,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("uuid") String articleUuid);
}