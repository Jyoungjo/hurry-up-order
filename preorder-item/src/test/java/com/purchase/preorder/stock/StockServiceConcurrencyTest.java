package com.purchase.preorder.stock;

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
        redisService.setValues("stockOfItem:1", 10);
    }

    @AfterEach
    void deleteAll() {
        itemRepository.deleteAll();
        stockRepository.deleteAll();
        redisService.deleteValuesByKey("stockOfItem:1");
    }

    // CONCURRENCY TEST - 재고 저장 비동기 처리 Test duration: 199ms
    @DisplayName("재고 동시성 테스트")
    @Test
    void 동시성_테스트() throws InterruptedException {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
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

        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(10),
                () -> assertThat(failCount.get()).isEqualTo(990)
        );
    }
}
