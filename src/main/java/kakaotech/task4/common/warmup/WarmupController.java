package kakaotech.task4.common.warmup;

import io.swagger.v3.oas.annotations.Hidden;
import kakaotech.task4.common.warmup.dto.WarmupResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class WarmupController {
    static final String SECRET_HEADER = "X-Warmup-Secret";

    private final WarmupProperties warmupProperties;
    private final WarmupService warmupService;

    @PostMapping("/warmup")
    public ResponseEntity<WarmupResult> warmUp(
            @RequestHeader(value = SECRET_HEADER, required = false) String secret,
            @RequestParam(required = false) Integer count) {

        if (!warmupProperties.enabled() || !matches(secret)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(warmupService.warmUp(count));
    }

    private boolean matches(String secret) {
        if (secret == null) {
            return false;
        }

        return MessageDigest.isEqual(
                secret.getBytes(StandardCharsets.UTF_8),
                warmupProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
