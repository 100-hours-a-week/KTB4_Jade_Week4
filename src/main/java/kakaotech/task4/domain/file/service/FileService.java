package kakaotech.task4.domain.file.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.file.code.FileExceptionCode;
import kakaotech.task4.domain.file.dto.req.PresignedUrlRequest;
import kakaotech.task4.domain.file.dto.res.PresignedUrlResponse;
import kakaotech.task4.domain.file.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    static final String PROFILE_IMAGE_PREFIX = "profile";

    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public PresignedUrlResponse issueUploadUrl(PresignedUrlRequest request) {
        String contentType = normalize(request.contentType());
        String extension = resolveExtension(contentType);
        String key = createKey(extension);

        return new PresignedUrlResponse(presignPut(key, contentType), toPublicUrl(key));
    }

    private String normalize(String contentType) {
        return contentType.trim().toLowerCase();
    }

    private String resolveExtension(String contentType) {
        String extension = ALLOWED_IMAGE_TYPES.get(contentType);
        if (extension == null) {
            throw new CustomException(FileExceptionCode.UNSUPPORTED_CONTENT_TYPE);
        }
        return extension;
    }

    private String createKey(String extension) {
        return PROFILE_IMAGE_PREFIX + "/" + UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    private String presignPut(String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(s3Properties.presignedExpiration())
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    private String toPublicUrl(String key) {
        return s3Properties.publicBaseUrl() + "/" + key;
    }
}
