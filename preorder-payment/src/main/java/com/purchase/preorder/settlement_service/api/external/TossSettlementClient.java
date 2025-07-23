package com.purchase.preorder.settlement_service.api.external;

import com.purchase.preorder.payment_service.config.TossFeignConfig;
import com.purchase.preorder.settlement_service.dto.TossSettlementResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "tossSettlementClient", url = "${tossPayments.settlements.base-url}", configuration = TossFeignConfig.class)
public interface TossSettlementClient {
    @GetMapping
    List<TossSettlementResult> retrieve(@RequestParam String startDate, @RequestParam String endDate);
}
