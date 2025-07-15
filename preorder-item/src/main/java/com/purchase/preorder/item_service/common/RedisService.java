package com.purchase.preorder.item_service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void setValues(String key, int value) {
        redisTemplate.opsForValue().set(key, String.valueOf(value), Duration.ofMinutes(5L));
    }

    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return null;
        }
        return String.valueOf(values.get(key));
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
