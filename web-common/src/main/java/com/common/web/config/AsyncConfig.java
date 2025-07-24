package com.common.web.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Kafka 발행 전용 풀
     * CPU 바운드에 가깝고, I/O 대기는 거의 없음.
     */
    @Bean(name = "kafkaPublishTaskExecutor")
    public ThreadPoolTaskExecutor kafkaPublishTaskExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(4);
        exec.setQueueCapacity(50);
        exec.setThreadNamePrefix("kafka-pub-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

    /**
     * 비즈니스 로직 처리용 풀
     * DB I/O, 외부 API 호출 등 대기(W)가 많으므로 I/O 바운드 설정을 적용
     */
    @Bean(name = "businessEventTaskExecutor")
    public ThreadPoolTaskExecutor businessEventTaskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        // TODO W/C 측정해서 core * (1 + W/C) 로 max pool size 잡아주기
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(cores * 2);
        exec.setMaxPoolSize(cores * 6);
        exec.setQueueCapacity(200);
        exec.setThreadNamePrefix("biz-event-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

    @Override
    public Executor getAsyncExecutor() {
        return businessEventTaskExecutor();
    }
}