package kakaotech.task4.common.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.cors")
public record CorsProperties(
        List<String> allowedOrigins
) {
}