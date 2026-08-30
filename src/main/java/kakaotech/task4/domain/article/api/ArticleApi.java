package kakaotech.task4.domain.article.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.req.UpdateArticleRequest;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
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
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 값 누락", value = ArticleSwaggerErrorExamples.GLOBAL_400_001),
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
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.GLOBAL_422_001)))
    })
    ResponseEntity<?> createArticle(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Valid @RequestBody CreateArticleRequest request);

    @Operation(summary = "게시글 수정", description = "전달된 필드만 수정한다. 세 필드가 모두 null이면 400을 반환한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 수정 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "변경할 내용 없음 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "변경할 내용 없음", value = ArticleSwaggerErrorExamples.ARTICLE_400_001),
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
                                    @ExampleObject(name = "수정 권한 없음", value = ArticleSwaggerErrorExamples.ARTICLE_403_001),
                                    @ExampleObject(name = "CSRF Token 오류", value = AuthSwaggerErrorExamples.AUTH_403_002)
                            })),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.GLOBAL_422_001)))
    })
    ResponseEntity<?> updateArticle(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody UpdateArticleRequest request);

    @Operation(summary = "게시글 삭제", description = "게시글 삭제 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공",
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
                                    @ExampleObject(name = "삭제 권한 없음", value = ArticleSwaggerErrorExamples.ARTICLE_403_002),
                                    @ExampleObject(name = "CSRF Token 오류", value = AuthSwaggerErrorExamples.AUTH_403_002)
                            })),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001)))
    })
    ResponseEntity<?> deleteArticle(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid);

    @Operation(summary = "게시글 목록 조회",
            description = "최신순 커서 페이지네이션. 다음 페이지가 있으면 응답의 nextCursor를 cursor로 그대로 전달한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerSuccessExamples.ARTICLE_200_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            })),
            @ApiResponse(responseCode = "422", description = "size 범위 위반",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.GLOBAL_422_001_SIZE)))
    })
    ResponseEntity<?> getArticleList(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Parameter(description = "이전 응답의 nextCursor 값 (첫 페이지는 생략)",
                    example = "MjAyNi0wMS0wMVQwODowMDowMFp8MTI=")
            @RequestParam(required = false) String cursor,
            @Parameter(description = "조회 개수 (1~10)", example = "10")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "조회 개수는 최소 1개입니다.")
            @Max(value = 10, message = "조회 개수는 최대 10개입니다.")
            int size);

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 조회 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerSuccessExamples.ARTICLE_200_002))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            })),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleSwaggerErrorExamples.ARTICLE_404_001)))
    })
    ResponseEntity<?> getArticleDetail(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("uuid") String articleUuid);
}
