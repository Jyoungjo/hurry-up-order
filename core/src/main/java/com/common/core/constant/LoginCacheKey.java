package com.common.core.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginCacheKey {
    LOGIN_KEY("loginInfo", 1000, 900);

    LoginCacheKey(String cacheName, int maximumSize, int expireAfterWriteOfSeconds) {
        this.cacheName = cacheName;
        this.maximumSize = maximumSize;
        this.expireAfterWriteOfSeconds = expireAfterWriteOfSeconds;
    }

    /**
     *  cacheName : 캐시 이름
     *  maximumSize : 캐시 최대 사이즈
     *  expireAfterWriteOfSeconds : 캐시 만료 시간(TTL)
     */

    private String cacheName;
    private int maximumSize;
    private int expireAfterWriteOfSeconds;
}
