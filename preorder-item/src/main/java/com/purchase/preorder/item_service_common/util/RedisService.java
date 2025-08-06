package com.purchase.preorder.item_service_common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class RedisService {
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, Object> luaRedisTemplate;
    private final RedisScript<Long> stockIncreaseScript;
    private final RedisScript<Long> stockDecreaseScript;
    private final RedisScript<List<Object>> reserveStockScript;
    private final RedisScript<List<Object>> decreaseStockConfirmScript;
    private final RedisScript<List<Object>> stockReservationCancelScript;
    private final RedisScript<Boolean> stockRollbackScript;
    private final ObjectMapper objectMapper;

    public RedisService(
            @Qualifier("objectRedisTemplate") RedisTemplate<String, Object> objectRedisTemplate,
            @Qualifier("luaRedisTemplate") RedisTemplate<String, Object> luaRedisTemplate,
            RedisScript<Long> stockIncreaseScript,
            RedisScript<Long> stockDecreaseScript,
            RedisScript<List<Object>> reserveStockScript,
            RedisScript<List<Object>> decreaseStockConfirmScript,
            RedisScript<List<Object>> stockReservationCancelScript,
            RedisScript<Boolean> stockRollbackScript,
            ObjectMapper objectMapper
    ) {
        this.objectRedisTemplate = objectRedisTemplate;
        this.luaRedisTemplate = luaRedisTemplate;
        this.stockIncreaseScript = stockIncreaseScript;
        this.stockDecreaseScript = stockDecreaseScript;
        this.reserveStockScript = reserveStockScript;
        this.decreaseStockConfirmScript = decreaseStockConfirmScript;
        this.stockReservationCancelScript = stockReservationCancelScript;
        this.stockRollbackScript = stockRollbackScript;
        this.objectMapper = objectMapper;
    }

    // -------- Value operations --------

    public void setValues(String key, Object value) {
        objectRedisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5L));
    }

    public <T> T getValues(String key, Class<T> clazz) {
        Object obj = objectRedisTemplate.opsForValue().get(key);
        return objectMapper.convertValue(obj, clazz);
    }

    public void setValues(Map<String, Integer> values) {
        objectRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisSerializer stringSerializer = new StringRedisSerializer();
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                connection.stringCommands().set(
                        Objects.requireNonNull(stringSerializer.serialize(entry.getKey())),
                        Objects.requireNonNull(stringSerializer.serialize(entry.getValue().toString()))
                );
            }
            return null;
        });
    }

    public List<Object> multiGet(List<String> keys) {
        return objectRedisTemplate.opsForValue().multiGet(keys);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(objectRedisTemplate.hasKey(key));
    }

    public void delete(String key) {
        objectRedisTemplate.delete(key);
    }

    // -------- Hash operations --------

    public void putHash(String key, String hashKey, Object value) {
        objectRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void deleteHash(String key, String hashKey) {
        objectRedisTemplate.opsForHash().delete(key, hashKey);
    }

    // -------- ZSet operations --------

    public Set<String> rangeByScore(String key, double min, double max) {
        return Objects.requireNonNull(luaRedisTemplate.opsForZSet().rangeByScore(key, min, max)).stream()
                .map(o -> (String) o)
                .collect(Collectors.toSet());
    }

    public void deleteZSetValues(String zsetKey, long now) {
        objectRedisTemplate.opsForZSet().removeRangeByScore(zsetKey, 0, now);
    }

    // -------- Script operations --------

    public Long adjustStockAtomically(String key, int value) {
        RedisScript<Long> selectedScript = value > 0 ? stockIncreaseScript : stockDecreaseScript;
        return executeScript(selectedScript, List.of(key), String.valueOf(value));
    }

    public List<Object> reserveStockAtomically(List<String> keys, String[] args) {
        return executeScript(reserveStockScript, keys, args);
    }

    public List<Object> decreaseConfirmStocks(List<String> keys, String[] args) {
        return executeScript(decreaseStockConfirmScript, keys, args);
    }

    public List<Object> cancelReservationStocks(List<String> keys, String[] args) {
        return executeScript(stockReservationCancelScript, keys, args);
    }

    public Boolean rollbackStocks(List<String> keys, String[] args) {
        return executeScript(stockRollbackScript, keys, args);
    }

    private <T> T executeScript(RedisScript<T> script, List<String> keys, Object... args) {
        List<String> strArgs = Stream.of(args)
                .map(Object::toString)
                .toList();

        try {
            return luaRedisTemplate.execute(script, keys, strArgs.toArray());
        } catch (Exception e) {
            log.warn("예외 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // -------- Utilities --------
}
