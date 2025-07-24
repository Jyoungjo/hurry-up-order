package com.purchase.preorder.user_service_common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
@Getter
public class LuaScriptConfig {

    @Bean
    public RedisScript<Boolean> stockIncreaseScript() throws IOException {
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        script.setScriptText(loadScript("lua/swap-token.lua"));
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
