package com.purchase.preorder.payment_service_common.config;

import feign.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class TossFeignConfig extends FeignConfig {
    @Value("${tossPayments.secret-key}")
    private String tossSecretKey;

    @Bean(name = "tossAuthInterceptor")
    @Override
    public RequestInterceptor authInterceptor() {
        return template -> {
            final String header = createAuthorizationHeader();
            template.header(AUTHORIZATION_HEADER, header);
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Override
    protected String createAuthorizationHeader() {
        final byte[] encodedBytes = Base64.getEncoder().encode((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return AUTH_HEADER_PREFIX + new String(encodedBytes);
    }

    /**
     * 기본 Feign Client를 래핑해서, execute() 호출 전후로
     * System.nanoTime()을 찍고 로그로 남깁니다.
     */
    @Bean
    public Client feignClient() {
        Client.Default delegate = new Client.Default(null, null);
        return new TimingFeignClient(delegate);
    }

    @Slf4j
    static class TimingFeignClient implements Client {
        private static final Path CSV_PATH = Paths.get("logs/feign-timing.csv");

        private final Client delegate;

        TimingFeignClient(Client delegate) {
            this.delegate = delegate;
            initCsv();
        }

        @Override
        public Response execute(Request request, Request.Options options) throws IOException {
            long start = System.nanoTime();
            Response response = delegate.execute(request, options);
            long tookMs = (System.nanoTime() - start) / 1_000_000;
            log.info("Feign [{} {}] call took {} ms", request.httpMethod(), request.url(), tookMs);
            appendCsv(Instant.now(), request.httpMethod().name(), request.url(), tookMs);
            return response;
        }

        private void initCsv() {
            try {
                if (Files.notExists(CSV_PATH)) {
                    Files.createDirectories(CSV_PATH.getParent());
                    try (PrintWriter pw = new PrintWriter(
                            Files.newBufferedWriter(CSV_PATH, StandardOpenOption.CREATE))) {
                        pw.println("timestamp,method,url,latency_ms");
                    }
                }
            } catch (IOException e) {
                log.error("CSV 초기화 실패", e);
            }
        }

        private synchronized void appendCsv(Instant ts, String method, String url, long tookMs) {
            String line = String.format("%s,%s,%s,%d",
                    DateTimeFormatter.ISO_INSTANT.format(ts),
                    method, url, tookMs);
            try {
                Files.write(CSV_PATH,
                        (line + System.lineSeparator()).getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error("CSV 기록 실패", e);
            }
        }
    }
}
