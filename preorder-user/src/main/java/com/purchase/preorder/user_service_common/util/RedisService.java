package com.purchase.preorder.user_service_common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> lua;
    private final RedisScript<Boolean> swapTokenScript;
    private final ObjectMapper objectMapper;

    public RedisService(@Qualifier("objectRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                        @Qualifier("luaRedisTemplate") RedisTemplate<String, String> lua,
                        RedisScript<Boolean> swapTokenScript,
                        ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.lua = lua;
        this.swapTokenScript = swapTokenScript;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void setValues(String key, Object value, Duration expiration) {
        redisTemplate.opsForValue().set(key, value, expiration);
    }

    public <T> T getValues(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        return objectMapper.convertValue(obj, clazz);
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

    public Boolean swapToken(String email, String refreshToken, long expireSeconds) {
        return executeScript(swapTokenScript, List.of(email, refreshToken), new String[]{String.valueOf(expireSeconds)});
    }

    private <T> T executeScript(RedisScript<T> script, List<String> keys, Object... args) {
        List<String> strArgs = Stream.of(args)
                .map(Object::toString)
                .toList();
        return lua.execute(script, keys, strArgs.toArray());
    }
}
