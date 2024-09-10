package web.common.util;

import java.util.UUID;

/**
 * @Project : SCUP
 * @Class : UUIDGenerator.java
 * @Description : UUID 생성 클래스.
 * @Author : im7015
 * @Since : 2019. 5. 10
 */
public final class UUIDGenerator {
    private static final Object MUTEX = new Object();

    private UUIDGenerator() {}

    /**
     * 36 Bytes 체계의 UUID 를 생성 하여 반환
     *
     * @return UUID 36 Bytes 체계의 UUID
     * @see <a href="https://en.wikipedia.org/wiki/Universally_unique_identifier">UUID on Wiki</a>
     */
    private static String uuid() {
        synchronized (MUTEX) {
            return UUID.randomUUID().toString();
        }
    }

    /**
     * 32 Bytes 체계의 UUID 를 생성 하여 반환 (하이픈 '-' 제거)
     *
     * @return UUID
     */
    public static String get() {
        return uuid().replace("-", "");
    }
}
