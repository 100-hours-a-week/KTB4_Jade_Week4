package kakaotech.task4.common.warmup;

import io.swagger.v3.oas.annotations.Hidden;
import kakaotech.task4.common.warmup.dto.WarmupResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class WarmupController {
    static final String SECRET_HEADER = "X-Warmup-Secret";

    private final WarmupService warmupService;

    @PostMapping("/warmup")
    public ResponseEntity<WarmupResult> warmUp(
            @RequestHeader(value = SECRET_HEADER, required = false) String secret,
            @RequestParam(required = false) Integer count) {

        WarmupResult response = warmupService.warmUp(secret, count);
        return ResponseEntity.ok(response);
    }
}
