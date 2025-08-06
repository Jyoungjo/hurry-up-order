package com.purchase.preorder.item_service_common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Getter
public class LuaScriptConfig {

    @Bean
    public RedisScript<Long> stockIncreaseScript() throws IOException {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(loadScript("lua/stock-increase.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public RedisScript<Long> stockDecreaseScript() throws IOException {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(loadScript("lua/stock-decrease.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public RedisScript<List<Object>> reserveStockScript() throws IOException {
        DefaultRedisScript<List<Object>> script = new DefaultRedisScript<>();
        script.setScriptText(loadScript("lua/reserve-stock.lua"));
        script.setResultType((Class) List.class);
        return script;
    }

    @Bean
    public RedisScript<List<Object>> decreaseStockConfirmScript() throws IOException {
        DefaultRedisScript<List<Object>> script = new DefaultRedisScript<>();
        script.setScriptText(loadScript("lua/stock-decrease-confirm.lua"));
        script.setResultType((Class) List.class);
        return script;
    }

    @Bean
    public RedisScript<List<Object>> stockReservationCancelScript() throws IOException {
        DefaultRedisScript<List<Object>> script = new DefaultRedisScript<>();
        script.setScriptText(loadScript("lua/stock-reservation-canceled.lua"));
        script.setResultType((Class) List.class);
        return script;
    }

    @Bean
    public RedisScript<Boolean> stockRollbackScript() throws IOException {
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        script.setScriptText(loadScript("lua/stock-rollback.lua"));
        script.setResultType(Boolean.class);
        return script;
    }

    private String loadScript(String path) throws IOException {
        return new String(
                new ClassPathResource(path).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
    }
}
