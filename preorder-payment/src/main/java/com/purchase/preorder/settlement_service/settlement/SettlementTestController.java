package com.purchase.preorder.settlement_service.settlement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-service/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementTestController {

    private final SettlementService settlementService;

    @PutMapping("/batch")
    public ResponseEntity<Void> test() {
        settlementService.runDailySettlementBatch();
        return ResponseEntity.noContent().build();
    }
}
