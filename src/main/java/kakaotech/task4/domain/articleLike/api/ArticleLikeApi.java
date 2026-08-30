package kakaotech.task4.domain.articleLike.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.article.api.ArticleSwaggerErrorExamples;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[게시물 좋아요 API]", description = "게시물 좋아요 API")
public interface ArticleLikeApi {

    @Operation(summary = "좋아요 누르기", description = "이미 좋아요를 누른 게시글이면 카운트를 바꾸지 않고 현재 상태를 반환한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleLikeSwaggerSuccessExamples.LIKE_200))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            })),
            @ApiResponse(responseCode = "403", description = "CSRF Token 없음 또는 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_403_002))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001))),
            @ApiResponse(responseCode = "409", description = "동시 요청으로 중복 좋아요 시도",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleLikeSwaggerErrorExamples.COMMON_409)))
    })
    ResponseEntity<?> like(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid);

    @Operation(summary = "좋아요 취소", description = "게시글 좋아요 취소 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleLikeSwaggerSuccessExamples.UNLIKE_200))),
            @ApiResponse(responseCode = "400", description = "좋아요 누르지 않은 게시글",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleLikeSwaggerErrorExamples.ARTICLE_LIKE_400_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            })),
            @ApiResponse(responseCode = "403", description = "CSRF Token 없음 또는 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_403_002))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001)))
    })
    ResponseEntity<?> unlike(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid);
}
