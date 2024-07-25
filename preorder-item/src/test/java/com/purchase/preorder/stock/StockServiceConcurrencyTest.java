package com.purchase.preorder.stock;

import com.purchase.preorder.common.RedisCacheKey;
import com.purchase.preorder.common.RedisService;
import com.purchase.preorder.config.RedisEmbeddedConfig;
import com.purchase.preorder.item.Item;
import com.purchase.preorder.item.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(RedisEmbeddedConfig.class)
public class StockServiceConcurrencyTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private StockService stockService;

    @BeforeEach
    void createStock() {
        Item item = itemRepository.saveAndFlush(Item.builder().id(1L).build());
        stockRepository.saveAndFlush(new Stock(1L, item, 10, null));
        redisService.setValues(RedisCacheKey.STOCK_KEY_PREFIX + "1", 10);
    }

    @AfterEach
    void deleteAll() {
        itemRepository.deleteAll();
        stockRepository.deleteAll();
        redisService.deleteValuesByKey(RedisCacheKey.STOCK_KEY_PREFIX + "1");
    }

    // CONCURRENCY TEST
    @DisplayName("재고 동시성 테스트")
    @Test
    void 동시성_테스트() throws InterruptedException {
        int executeCount = 10000;
        int numOfThread = 32;
        int expectedSuccessCount = 10;
        int expectedFailCount = executeCount - expectedSuccessCount;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThread);
        CountDownLatch countDownLatch = new CountDownLatch(executeCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < executeCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseStock(1L, 1);
                    successCount.getAndIncrement();
                    System.out.println(Thread.currentThread().getName() + " succeeded.");
                } catch (Exception e) {
                    failCount.getAndIncrement();
                    System.out.println(Thread.currentThread().getName() + " failed: " + e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        long stopTime = System.currentTimeMillis();
        long diff = stopTime - startTime;
        System.out.printf("""
                        Thread 개수: %d \n
                        실행 횟수: %d회 \n
                        예상 재고 감소량: %d개 \n
                        실제 재고 감소량: %d개 \n
                        예상 실패량: %d개 \n
                        실제 실패량: %d개 \n
                        테스트 경과 시간: %d ms
                        """,
                numOfThread, executeCount, expectedSuccessCount, successCount.get(),
                expectedFailCount, failCount.get(), diff
        );

        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(expectedSuccessCount),
                () -> assertThat(failCount.get()).isEqualTo(expectedFailCount)
        );
    }
}
