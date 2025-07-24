package com.purchase.preorder.payment_service.payment.processor;

import com.purchase.preorder.payment_service_common.util.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPaymentPGProcessor<T, C> implements PaymentPGProcessor<T, C> {

    protected final RedisService redisService;

    protected <R> R getOrRequest(String redisKey, Class<R> clazz, Supplier<R> fetcher) {
        R cached = redisService.getValues(redisKey, clazz);
        if (cached != null) {
            log.info("캐시된 PG 결과 사용 - key: {}", redisKey);
            return cached;
        }

        R response = fetcher.get();
        redisService.setValues(redisKey, response);
        return response;
    }
}
