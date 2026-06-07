package kakaotech.task4.domain.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.domain.auth.dto.req.SignInRequest;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
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
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.SIGN_UP_400))),
            @ApiResponse(responseCode = "409", description = "중복 데이터",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.SIGN_UP_409))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "비밀번호 불일치", value = AuthSwaggerErrorExamples.SIGN_UP_422_PASSWORD),
                                    @ExampleObject(name = "필드 유효성 검사 실패", value = AuthSwaggerErrorExamples.SIGN_UP_422_VALIDATION)
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
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.SIGN_IN_400))),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.SIGN_IN_401))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.SIGN_IN_422)))
    })
    ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request);
}