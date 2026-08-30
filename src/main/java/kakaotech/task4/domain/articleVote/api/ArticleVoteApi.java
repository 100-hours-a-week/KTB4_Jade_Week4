package kakaotech.task4.domain.articleVote.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.article.api.ArticleSwaggerErrorExamples;
import kakaotech.task4.domain.articleVote.dto.req.CreateVoteRequest;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[게시물 투표 API]", description = "밸런스 게임 투표 API")
public interface ArticleVoteApi {

    @Operation(summary = "투표하기",
            description = """
                    선택지에 투표한다. 최초 투표면 해당 선택지 +1, 다른 선택지면 이전 선택지 -1 후 새 선택지 +1.
                    이미 선택한 선택지를 다시 보내면 카운트를 바꾸지 않고 changed=false 로 응답한다.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표 성공",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "최초 투표", value = ArticleVoteSwaggerSuccessExamples.VOTE_200_001),
                                    @ExampleObject(name = "선택지 변경", value = ArticleVoteSwaggerSuccessExamples.VOTE_200_002),
                                    @ExampleObject(name = "같은 선택지 재요청 (변화 없음)", value = ArticleVoteSwaggerSuccessExamples.VOTE_200_003)
                            })),
            @ApiResponse(responseCode = "400", description = "본문 형식 오류 (A, B 외의 값 등)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.GLOBAL_400_002))),
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
            @ApiResponse(responseCode = "409", description = "동시 요청으로 중복 투표 시도",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleVoteSwaggerErrorExamples.COMMON_409))),
            @ApiResponse(responseCode = "422", description = "선택지 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ArticleVoteSwaggerErrorExamples.GLOBAL_422_001)))
    })
    ResponseEntity<?> vote(
            @Parameter(hidden = true) @CurrentMember AuthenticatedMember member,
            @Parameter(description = "게시글 UUID", required = true) @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody CreateVoteRequest request);
}
