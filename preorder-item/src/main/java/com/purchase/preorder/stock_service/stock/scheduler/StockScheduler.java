package com.purchase.preorder.stock_service.stock.scheduler;

import com.common.core.util.RedisKeyHelper;
import com.purchase.preorder.item_service_common.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StockScheduler {

    private final RedisService redisService;

    @Scheduled(fixedDelay = 10000) // 10초 마다
    public void cleanupExpiredReservations() {
        String zsetKey = RedisKeyHelper.reservedTtlKey();
        long now = System.currentTimeMillis() / 1000;

        Set<String> expired = redisService.rangeByScore(zsetKey, 0, now);
        if (expired.isEmpty()) return;

        for (String member : expired) {
            String[] splited = member.split(":");
            if (splited.length != 2) continue;

            String itemId = splited[0];
            String userId = splited[1];
            String key = RedisKeyHelper.reservedItemKey(itemId);

            redisService.deleteHash(key, userId);
        }

        redisService.deleteZSetValues(zsetKey, now);
    }
}
