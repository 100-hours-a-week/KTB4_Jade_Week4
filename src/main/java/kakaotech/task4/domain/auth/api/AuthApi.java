package kakaotech.task4.domain.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.auth.dto.req.SignInRequest;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthApi {

    @Operation(summary = "회원가입", description = "회원가입 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerSuccessExamples.SIGN_UP_201))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_400_001))),
            @ApiResponse(responseCode = "409", description = "중복 데이터",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_409_001))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "비밀번호 불일치", value = AuthSwaggerErrorExamples.AUTH_422_001),
                                    @ExampleObject(name = "필드 유효성 검사 실패", value = AuthSwaggerErrorExamples.GLOBAL_422_001)
                            }))
    })
    ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request);

    @Operation(summary = "로그인", description = "로그인 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerSuccessExamples.SIGN_IN_200))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_400_002))),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_002))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_422_002)))
    })
    ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request);

    @Operation(summary = "로그아웃", description = "로그아웃 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공",
                    content = @Content)
    })
    ResponseEntity<?> signOut(@Parameter(description = "유저 UUID", required = true) @CurrentMember Member member);
}