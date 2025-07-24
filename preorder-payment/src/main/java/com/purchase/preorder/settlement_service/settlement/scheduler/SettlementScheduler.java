package com.purchase.preorder.settlement_service.settlement.scheduler;

import com.purchase.preorder.settlement_service.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementScheduler {

    private final SettlementService settlementService;

    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시에 실행
    public void runDailySettlement() {
        settlementService.runDailySettlementBatch();
    }
}
