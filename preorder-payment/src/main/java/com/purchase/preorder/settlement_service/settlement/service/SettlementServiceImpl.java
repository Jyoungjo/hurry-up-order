package com.purchase.preorder.settlement_service.settlement.service;

import com.common.domain.common.SettlementStatus;
import com.common.domain.entity.payment.PendingSettlement;
import com.common.domain.entity.payment.Settlement;
import com.common.domain.repository.payment.SettlementRepository;
import com.common.event_common.domain_event_vo.payment.PaymentSucceedDomainEvent;
import com.purchase.preorder.settlement_service.api.external.pg.TossSettlementClient;
import com.purchase.preorder.settlement_service.api.external.pg.dto.TossSettlementResult;
import com.purchase.preorder.settlement_service.settlement.repository.SettlementJDBCRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private static final String TOSS_PAYMENTS = "tvivarepublica";

    private final SettlementRepository settlementRepository;
    private final SettlementJDBCRepository jdbcRepository;
    private final TossSettlementClient tossSettlementClient;
//    private final NiceSettlementClient niceSettlementClient;

    @Override
    @Transactional
    public void create(PaymentSucceedDomainEvent event) {
        Settlement settlement = Settlement.of(
                event.getPaymentId(),
                event.getOrderId(),
                event.getPgOrderId(),
                event.getPgName(),
                event.getTotalAmount(),
                event.getFeeAmount(),
                event.getSettledAmount(),
                event.getSoldAt()
        );

        settlementRepository.save(settlement);
    }

    /*
     * TODO 현재 나이스페이 정산의 경우 Open API 존재하지 않음
     *  우선 토스만 적용 -> 추후 서브 PG사 변경 필요 및 병렬 작업 추가
     */

    @Override
    @Transactional
    public void runDailySettlementBatch() {
        LocalDateTime soldAt = LocalDateTime.now().minusDays(5);
        List<PendingSettlement> pendings = settlementRepository.findByStatusAndPgNameAndSettledAtIsNullAndSoldAtLessThanEqual(
                SettlementStatus.PENDING, TOSS_PAYMENTS, soldAt
        );

        if (pendings.isEmpty()) {
            log.info("처리할 정산 없음");
            return;
        }

        Map<String, Long> pgToId = pendings.stream()
                .collect(Collectors.toMap(PendingSettlement::getPgOrderId, PendingSettlement::getId));

        List<TossSettlementResult> response;
        List<Long> settlementIds = new ArrayList<>();

        try {
            log.info("TOSS API 호출 시도");
            /*
             * 테스트 상점에서는 정산 조회 불가
             */

            // 결제일로부터 5일 뒤 정산 완료된다고 가정 -> 현재로부터 5일 전 결제가 진행된 정산 내역 확인
            String from = String.valueOf(soldAt.minusDays(1).toLocalDate());
            String to = String.valueOf(soldAt.toLocalDate());
            response = tossSettlementClient.retrieve(from, to);
        } catch (Exception e) {
            log.warn("TOSS API 호출 실패 - 원인: {}", e.getMessage());
            return;
        }

        log.info("TOSS API 호출 성공 -> 정산 확인");
        jdbcRepository.checkSettlement(pgToId, response);
    }

    @Override
    @Transactional
    public void reverseSettlement(Long paymentId) {
        settlementRepository.updateSettlementStatus(SettlementStatus.REVERSED, paymentId);
    }

    @Override
    public void delete(Long paymentId) {
        settlementRepository.deleteAllByPaymentIdIn(LocalDateTime.now(), paymentId);
    }
}
