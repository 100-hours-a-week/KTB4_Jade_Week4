package kakaotech.task4.domain.file.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties(
        String bucket,
        String region,
        String publicBaseUrl,
        Duration presignedExpiration
) {
}
