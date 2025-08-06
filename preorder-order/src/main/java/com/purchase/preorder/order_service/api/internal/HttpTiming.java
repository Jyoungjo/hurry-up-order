package com.purchase.preorder.order_service.api.internal;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class HttpTiming {

    private static final ThreadMXBean threadMx = ManagementFactory.getThreadMXBean();
    private static final Path LOG_PATH = Paths.get("http-timing.csv");
    private static volatile boolean headerWritten = false;

    // 누적합계와 호출 횟수를 기록할 Atomic 변수
    private static final AtomicLong totalSumMs = new AtomicLong(0);
    private static final AtomicLong callCount = new AtomicLong(0);

    /**
     * 외부 호출(W/C) 측정
     */
    public <T> T measureWaitCpu(Callable<T> httpCall) throws Exception {
        if (threadMx.isThreadCpuTimeSupported() && !threadMx.isThreadCpuTimeEnabled()) {
            threadMx.setThreadCpuTimeEnabled(true);
        }

        long startCpuNanos  = threadMx.getCurrentThreadCpuTime();
        long startWallNanos = System.nanoTime();

        T result = httpCall.call();

        long endWallNanos = System.nanoTime();
        long endCpuNanos  = threadMx.getCurrentThreadCpuTime();

        long totalNanos = endWallNanos - startWallNanos;
        long cpuNanos   = endCpuNanos  - startCpuNanos;
        long waitNanos  = totalNanos    - cpuNanos;

        double totalMs = totalNanos / 1_000_000.0;
        double cpuMs   = cpuNanos   / 1_000_000.0;
        double waitMs  = waitNanos  / 1_000_000.0;
        double ratio   = cpuNanos > 0 ? waitNanos / (double) cpuNanos : 0;

        // 누적값 업데이트
        long count = callCount.incrementAndGet();
        long sum   = totalSumMs.addAndGet((long) totalMs);
        double avgLatency = sum / (double) count;

        // 콘솔 출력
        System.out.printf(
                "HTTP call: total=%.2fms, CPU=%.2fms, WAIT=%.2fms, W/C=%.2f, AVG_LATENCY=%.2fms (over %d calls)%n",
                totalMs, cpuMs, waitMs, ratio, avgLatency, count
        );

        writeCsv(totalMs, cpuMs, waitMs, ratio, avgLatency, count);

        return result;
    }

    private void writeCsv(double total, double cpu, double wait, double wcratio,
                          double avgLatency, long count) {
        try {
            // 헤더 기록
            if (!headerWritten) {
                synchronized(HttpTiming.class) {
                    if (!headerWritten) {
                        Files.writeString(
                                LOG_PATH,
                                "timestamp,total_ms,cpu_ms,wait_ms,w_c_ratio,avg_latency_ms,call_count" + System.lineSeparator(),
                                StandardOpenOption.CREATE, StandardOpenOption.APPEND
                        );
                        headerWritten = true;
                    }
                }
            }
            // 데이터 라인 작성
            String line = String.format(
                    "%s,%.2f,%.2f,%.2f,%.2f,%.2f,%d%n",
                    Instant.now(), total, cpu, wait, wcratio, avgLatency, count
            );
            Files.writeString(LOG_PATH, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LoggerFactory.getLogger(HttpTiming.class)
                    .warn("Failed to write HTTP timing log", e);
        }
    }
}
