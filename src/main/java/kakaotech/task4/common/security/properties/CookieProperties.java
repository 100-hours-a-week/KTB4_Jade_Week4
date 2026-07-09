package kakaotech.task4.common.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.cookie")
public record CookieProperties(
        String accessName,
        String refreshName,
        boolean secure,
        String sameSite
) {
}