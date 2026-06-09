package kakaotech.task4.domain.myInfo.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "[마이페이지 API]", description = "마이페이지 관련 API")
public interface MyApi {

    @Operation(summary = "마이페이지 조회", description = "마이페이지 기본 정보 조회 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerSuccessExamples.MY_200_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001)))
    })
    ResponseEntity<?> getMyBasicInfo(
            @Parameter(description = "유저 UUID", required = true) @RequestHeader("Authorization") String userUuid);

    @Operation(summary = "마이페이지 수정", description = "마이페이지 기본 정보 수정 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 수정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerSuccessExamples.MY_200_002))),
            @ApiResponse(responseCode = "400", description = "변경할 내용 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerErrorExamples.MY_400_001))),
            @ApiResponse(responseCode = "401", description = "로그인 후 사용 가능",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = AuthSwaggerErrorExamples.AUTH_401_001))),
            @ApiResponse(responseCode = "409", description = "중복된 닉네임",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerErrorExamples.MY_409_001))),
            @ApiResponse(responseCode = "422", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = MySwaggerErrorExamples.MY_422_001)))
    })
    ResponseEntity<?> updateMyBasicInfo(
            @Parameter(description = "유저 UUID", required = true) @RequestHeader("Authorization") String userUuid,
            @Valid @RequestBody UpdateMyBasicInfoRequest request);
}