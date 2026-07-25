package kakaotech.task4.domain.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 값 누락", value = AuthSwaggerErrorExamples.GLOBAL_400_001_SIGN_UP),
                                    @ExampleObject(name = "본문 형식 오류", value = AuthSwaggerErrorExamples.GLOBAL_400_002)
                            })),
            @ApiResponse(responseCode = "409", description = "중복 데이터",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_409_001))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "비밀번호 확인 불일치", value = AuthSwaggerErrorExamples.AUTH_422_001),
                                    @ExampleObject(name = "필드 형식 위반", value = AuthSwaggerErrorExamples.GLOBAL_422_001_SIGN_UP)
                            }))
    })
    ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request);

    @Operation(summary = "로그인", description = "로그인 성공 시 Access Token / Refresh Token 쿠키를 내려준다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerSuccessExamples.SIGN_IN_200))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 값 누락", value = AuthSwaggerErrorExamples.GLOBAL_400_001_SIGN_IN),
                                    @ExampleObject(name = "본문 형식 오류", value = AuthSwaggerErrorExamples.GLOBAL_400_002)
                            })),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_002))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.GLOBAL_422_001_SIGN_IN)))
    })
    ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request,
                             @Parameter(hidden = true) HttpServletResponse response);

    @Operation(summary = "로그아웃", description = "Refresh Token을 삭제하고 토큰 쿠키를 만료시킨다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003),
                                    @ExampleObject(name = "회원 정보 없음", value = AuthSwaggerErrorExamples.AUTH_401_001)
                            })),
            @ApiResponse(responseCode = "403", description = "CSRF Token 없음 또는 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_403_002))),
            @ApiResponse(responseCode = "404", description = "Refresh Token 쿠키 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.JWT_404_001)))
    })
    ResponseEntity<?> signOut(@Parameter(hidden = true) @CurrentMember Member member,
                              @Parameter(hidden = true) HttpServletRequest request,
                              @Parameter(hidden = true) HttpServletResponse response);

    @Operation(summary = "토큰 재발급", description = "Refresh Token 쿠키로 Access Token과 Refresh Token을 재발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerSuccessExamples.TOKEN_REISSUE_200))),
            @ApiResponse(responseCode = "401", description = "Refresh Token 만료 또는 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Refresh Token 만료", value = AuthSwaggerErrorExamples.JWT_401_004),
                                    @ExampleObject(name = "Refresh Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_005)
                            })),
            @ApiResponse(responseCode = "403", description = "CSRF Token 없음 또는 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_403_002))),
            @ApiResponse(responseCode = "404", description = "Refresh Token 쿠키 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.JWT_404_001)))
    })
    ResponseEntity<?> reissue(@Parameter(hidden = true) HttpServletRequest request,
                              @Parameter(hidden = true) HttpServletResponse response);
}
