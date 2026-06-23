package kakaotech.task4.domain.articleLike.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.article.api.ArticleSwaggerErrorExamples;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[게시물 좋아요 API]", description = "게시물 좋아요 API")
public interface ArticleLikeApi {

    @Operation(summary = "좋아요 누르기", description = "게시글 좋아요 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "좋아요 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleLikeSwaggerSuccessExamples.LIKE_201))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001)))
    })
    ResponseEntity<?> like(
            @Parameter(description = "유저 UUID", required = true) @CurrentMember Member member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid);

    @Operation(summary = "좋아요 취소", description = "게시글 좋아요 취소 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "좋아요 취소 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleLikeSwaggerSuccessExamples.UNLIKE_201))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001))),
            @ApiResponse(responseCode = "409", description = "좋아요 누르지 않은 게시글",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleLikeSwaggerErrorExamples.ARTICLE_LIKE_409_001)))
    })
    ResponseEntity<?> unlike(
            @Parameter(description = "유저 UUID", required = true) @CurrentMember Member member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid);
}