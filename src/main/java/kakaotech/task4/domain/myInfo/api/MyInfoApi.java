package kakaotech.task4.domain.myInfo.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import kakaotech.task4.domain.file.api.FileSwaggerErrorExamples;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[마이페이지 API]", description = "마이페이지 관련 API")
public interface MyInfoApi {

    @Operation(summary = "마이페이지 조회", description = "마이페이지 기본 정보 조회 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerSuccessExamples.MY_200_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            }))
    })
    ResponseEntity<?> getMyBasicInfo(
            @Parameter(hidden = true) @CurrentMember Member member);

    @Operation(summary = "마이페이지 수정", description = "전달된 필드만 수정한다. 두 필드가 모두 null이면 400을 반환한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 수정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerSuccessExamples.MY_200_002))),
            @ApiResponse(responseCode = "400", description = "변경할 내용 없음, 프로필 이미지 주소 오류 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "변경할 내용 없음", value = MySwaggerErrorExamples.MY_400_001),
                                    @ExampleObject(name = "프로필 이미지 주소 오류", value = FileSwaggerErrorExamples.FILE_400_002),
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
            @ApiResponse(responseCode = "409", description = "중복된 닉네임",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerErrorExamples.MY_409_001))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerErrorExamples.GLOBAL_422_001_NICKNAME)))
    })
    ResponseEntity<?> updateMyBasicInfo(
            @Parameter(hidden = true) @CurrentMember Member member,
            @Valid @RequestBody UpdateMyBasicInfoRequest request);

    @Operation(summary = "보안 정보 수정", description = "마이페이지 비밀번호 수정 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "비밀번호 수정 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 값 누락", value = MySwaggerErrorExamples.GLOBAL_400_001),
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
                            examples = {
                                    @ExampleObject(name = "비밀번호 형식 위반", value = MySwaggerErrorExamples.GLOBAL_422_001_PASSWORD),
                                    @ExampleObject(name = "현재 비밀번호 불일치", value = MySwaggerErrorExamples.MY_422_001_NOW_PASSWORD),
                                    @ExampleObject(name = "변경할 비밀번호 확인 불일치", value = MySwaggerErrorExamples.MY_422_001_PASSWORD_MISMATCH)
                            }))
    })
    ResponseEntity<?> updateMySecurity(
            @Parameter(hidden = true) @CurrentMember Member member,
            @Valid @RequestBody UpdateMySecurityRequest request);

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 api")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Access Token 없음", value = AuthSwaggerErrorExamples.JWT_401_001),
                                    @ExampleObject(name = "Access Token 만료", value = AuthSwaggerErrorExamples.JWT_401_002),
                                    @ExampleObject(name = "Access Token 유효하지 않음", value = AuthSwaggerErrorExamples.JWT_401_003)
                            })),
            @ApiResponse(responseCode = "403", description = "CSRF Token 없음 또는 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_403_002)))
    })
    ResponseEntity<?> deleteAccount(
            @Parameter(hidden = true) @CurrentMember Member member);
}
