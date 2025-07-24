package com.purchase.preorder.payment_service_common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void setValues(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5L));
    }

    public <T> T getValues(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        return objectMapper.convertValue(obj, clazz);
    }

    public Long increment(String key, int value) {
        return redisTemplate.opsForValue().increment(key, value);
    }

    @Transactional
    public void deleteValuesByKey(String key) {
        redisTemplate.delete(key);
    }

    public boolean checkExistsValue(String value) {
        Set<String> keys = redisTemplate.keys("*");
        if (keys == null) {
            return false;
        }

        for (String key : keys) {
            Object targetValue = redisTemplate.opsForValue().get(key);
            if (value.equals(targetValue)) return true;
        }
        return false;
    }
}
