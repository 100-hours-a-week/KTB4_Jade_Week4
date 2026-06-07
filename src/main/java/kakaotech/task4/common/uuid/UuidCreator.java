package kakaotech.task4.common.uuid;

import java.util.UUID;

public final class UuidCreator {
    private UuidCreator() {}

    public static String create(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}