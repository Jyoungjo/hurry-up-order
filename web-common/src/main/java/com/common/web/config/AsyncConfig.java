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
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(cores + 1);
        exec.setMaxPoolSize(cores + 2);
        exec.setQueueCapacity(10);
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
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(3);
        exec.setMaxPoolSize(6);
        exec.setQueueCapacity(0);
        exec.setThreadNamePrefix("biz-event-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

    /**
     * Feign Client 전용 풀
     * Feign Client 의 경우, blocking 통신이기 때문에 CPU 작업 시간을 측정할 수 없다.
     * 그래서 목표 처리량 기준으로 풀 사이즈 지정
     */
    @Bean(name = "feignClientTaskExecutor")
    public ThreadPoolTaskExecutor feignClientTaskExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(6);
        exec.setQueueCapacity(20);
        exec.setThreadNamePrefix("feign-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

    @Override
    public Executor getAsyncExecutor() {
        return businessEventTaskExecutor();
    }
}