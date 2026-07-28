package kakaotech.task4.domain.file.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.file.code.FileExceptionCode;
import kakaotech.task4.domain.file.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileImageUrlValidator {
    private static final String PROFILE_PATH = "/" + FileService.PROFILE_IMAGE_PREFIX + "/";
    private final S3Properties s3Properties;

    public void validate(String profileImageUrl) {
        if (profileImageUrl == null) {
            return;
        }
        if (!profileImageUrl.startsWith(allowedPrefix())) {
            throw new CustomException(FileExceptionCode.INVALID_PROFILE_IMAGE_URL);
        }
    }

    private String allowedPrefix() {
        return s3Properties.publicBaseUrl() + PROFILE_PATH;
    }
}
