package kakaotech.task4.domain.file.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kakaotech.task4.common.response.ApiResponse;
import kakaotech.task4.domain.file.api.FileApi;
import kakaotech.task4.domain.file.dto.req.PresignedUrlRequest;
import kakaotech.task4.domain.file.dto.res.PresignedUrlResponse;
import kakaotech.task4.domain.file.service.FileService;
import kakaotech.task4.domain.file.service.UploadUrlRateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class FileController implements FileApi {
    private final FileService fileService;
    private final UploadUrlRateLimiter uploadUrlRateLimiter;

    @PostMapping("/image-uploads")
    @Override
    public ResponseEntity<?> issueUploadUrl(@Valid @RequestBody PresignedUrlRequest request,
                                            HttpServletRequest httpRequest) {
        uploadUrlRateLimiter.check(httpRequest);

        PresignedUrlResponse response = fileService.issueUploadUrl(request);
        return ApiResponse.success(response).toEntity();
    }
}
