package kakaotech.task4.domain.comment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.domain.article.api.ArticleSwaggerErrorExamples;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.req.UpdateCommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@Tag(name = "[댓글 API]", description = "댓글 관련 API")
public interface CommentApi {

    @Operation(summary = "댓글 작성", description = "댓글 작성 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CommentSwaggerSuccessExamples.CREATE_COMMENT_201))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CommentSwaggerErrorExamples.COMMENT_400_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001)))
    })
    ResponseEntity<?> createComment(
            @Parameter(description = "유저 UUID", required = true) @RequestHeader("Authorization") String userUuid,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody CreateCommentRequest request);

    @Operation(summary = "댓글 수정", description = "댓글 수정 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 수정 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "필수 값 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CommentSwaggerErrorExamples.COMMENT_400_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CommentSwaggerErrorExamples.COMMENT_403_001))),
            @ApiResponse(responseCode = "404", description = "게시글 또는 댓글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "게시글 없음", value = ArticleSwaggerErrorExamples.ARTICLE_404_001),
                                    @ExampleObject(name = "댓글 없음", value = CommentSwaggerErrorExamples.COMMENT_404_001)
                            }))
    })
    ResponseEntity<?> updateComment(
            @Parameter(description = "유저 UUID", required = true) @RequestHeader("Authorization") String userUuid,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid,
            @Parameter(description = "댓글 UUID", required = true) @PathVariable("comment-uuid") String commentUuid,
            @Valid @RequestBody UpdateCommentRequest request);
}