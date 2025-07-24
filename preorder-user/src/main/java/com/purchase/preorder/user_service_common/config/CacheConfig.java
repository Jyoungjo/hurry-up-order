package com.purchase.preorder.user_service_common.config;

import com.common.core.constant.LoginCacheKey;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(){
        LoginCacheKey loginCache = LoginCacheKey.LOGIN_KEY;
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(loginCache.getCacheName());
        caffeineCacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(loginCache.getMaximumSize())
                        .expireAfterWrite(Duration.ofSeconds(loginCache.getExpireAfterWriteOfSeconds()))
                        .recordStats());

        return caffeineCacheManager;
    }
}
