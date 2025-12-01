package com.irum.paymentservice.global.util;

import java.util.UUID;

public class IdempotencyKeyUtil {
    /**
     * UUID v4 형식의 고유한 멱등키를 생성합니다.
     * @return String 형태의 멱등키
     */
    public static String generateKey() {
        return UUID.randomUUID().toString();
    }
}
