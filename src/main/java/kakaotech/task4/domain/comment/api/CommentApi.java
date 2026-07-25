package kakaotech.task4.domain.comment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.article.api.ArticleSwaggerErrorExamples;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.req.UpdateCommentRequest;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "[댓글 API]", description = "댓글 관련 API")
public interface CommentApi {

    @Operation(summary = "댓글 작성", description = "댓글 작성 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CommentSwaggerSuccessExamples.CREATE_COMMENT_201))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 값 누락", value = CommentSwaggerErrorExamples.GLOBAL_400_001),
                                    @ExampleObject(name = "본문 형식 오류", value = AuthSwaggerErrorExamples.GLOBAL_400_002)
                            })),
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
    ResponseEntity<?> createComment(
            @Parameter(hidden = true) @CurrentMember Member member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody CreateCommentRequest request);

    @Operation(summary = "댓글 수정", description = "댓글 수정 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 수정 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 값 누락", value = CommentSwaggerErrorExamples.GLOBAL_400_001),
                                    @ExampleObject(name = "본문 형식 오류", value = AuthSwaggerErrorExamples.GLOBAL_400_002)
                            })),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            })),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음 또는 CSRF Token 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "수정 권한 없음", value = CommentSwaggerErrorExamples.COMMENT_403_001),
                                    @ExampleObject(name = "CSRF Token 오류", value = AuthSwaggerErrorExamples.AUTH_403_002)
                            })),
            @ApiResponse(responseCode = "404", description = "게시글 또는 댓글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "게시글 없음", value = ArticleSwaggerErrorExamples.ARTICLE_404_001),
                                    @ExampleObject(name = "댓글 없음", value = CommentSwaggerErrorExamples.COMMENT_404_001)
                            }))
    })
    ResponseEntity<?> updateComment(
            @Parameter(hidden = true) @CurrentMember Member member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid,
            @Parameter(description = "댓글 UUID", required = true) @PathVariable("comment-uuid") String commentUuid,
            @Valid @RequestBody UpdateCommentRequest request);

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            })),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음 또는 CSRF Token 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "삭제 권한 없음", value = CommentSwaggerErrorExamples.COMMENT_403_002),
                                    @ExampleObject(name = "CSRF Token 오류", value = AuthSwaggerErrorExamples.AUTH_403_002)
                            })),
            @ApiResponse(responseCode = "404", description = "게시글 또는 댓글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "게시글 없음", value = ArticleSwaggerErrorExamples.ARTICLE_404_001),
                                    @ExampleObject(name = "댓글 없음", value = CommentSwaggerErrorExamples.COMMENT_404_001)
                            }))
    })
    ResponseEntity<?> deleteComment(
            @Parameter(hidden = true) @CurrentMember Member member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid,
            @Parameter(description = "댓글 UUID", required = true) @PathVariable("comment-uuid") String commentUuid);
}
