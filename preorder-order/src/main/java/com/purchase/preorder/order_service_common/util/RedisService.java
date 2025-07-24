package com.purchase.preorder.order_service_common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void setValues(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }
        return (String) values.get(key);
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
