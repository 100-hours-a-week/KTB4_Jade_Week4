package kakaotech.task4.domain.file.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kakaotech.task4.domain.auth.api.AuthSwaggerErrorExamples;
import kakaotech.task4.domain.file.dto.req.PresignedUrlRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[파일 API]", description = "이미지 업로드 관련 API")
public interface FileApi {

    @Operation(
            summary = "이미지 업로드 URL 발급",
            description = """
                    S3에 이미지를 직접 올릴 수 있는 임시 URL(uploadUrl)을 발급한다.
                    클라이언트는 uploadUrl로 이미지 바이너리를 PUT한 뒤,
                    함께 받은 fileUrl을 마이페이지 수정 API의 profileImageUrl로 전달한다.
                    PUT 요청의 Content-Type은 발급 요청에 보낸 contentType과 같아야 한다.

                    회원가입 화면에서도 써야 하므로 로그인 없이 호출할 수 있다.
                    대신 IP당 1시간에 10회로 발급 횟수가 제한된다.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 URL 발급 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = FileSwaggerSuccessExamples.FILE_200_001))),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 형식 또는 본문 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "지원하지 않는 이미지 형식", value = FileSwaggerErrorExamples.FILE_400_001),
                                    @ExampleObject(name = "필수 값 누락", value = FileSwaggerErrorExamples.GLOBAL_400_001),
                                    @ExampleObject(name = "본문 형식 오류", value = AuthSwaggerErrorExamples.GLOBAL_400_002)
                            })),
            @ApiResponse(responseCode = "429", description = "발급 횟수 초과",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = FileSwaggerErrorExamples.FILE_429_001)))
    })
    ResponseEntity<?> issueUploadUrl(@Valid @RequestBody PresignedUrlRequest request,
                                     @Parameter(hidden = true) HttpServletRequest httpRequest);
}
