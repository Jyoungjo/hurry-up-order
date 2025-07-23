package com.purchase.preorder.settlement_service.settlement;

import com.common.core.exception.ExceptionCode;
import com.common.domain.common.SettlementStatus;
import com.common.domain.entity.Settlement;
import com.common.domain.repository.SettlementRepository;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.payment_service.event.PaymentSucceedEvent;
import com.purchase.preorder.settlement_service.api.external.TossSettlementClient;
import com.purchase.preorder.settlement_service.dto.TossSettlementResult;
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
    private final TossSettlementClient tossSettlementClient;
//    private final NiceSettlementClient niceSettlementClient;

    @Override
    @Transactional
    public void create(PaymentSucceedEvent event) {
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
        List<Settlement> settlements = settlementRepository.findByStatusAndSettledAtIsNullAndSoldAtLessThanEqual(
                SettlementStatus.PENDING, LocalDateTime.now()
        );

        Map<String, Settlement> settlementMap = settlements.stream()
                .filter(s -> s.getPgOrderId() != null && s.getPgName().equals(TOSS_PAYMENTS))
                .collect(Collectors.toMap(Settlement::getPgOrderId, s -> s, (a, b) -> a));

        List<TossSettlementResult> response;
        List<Long> settlementIds = new ArrayList<>();

        try {
            log.info("TOSS API 호출 시도");
            /*
             * 테스트 상점에서는 정산 조회 불가
             */

            // 결제일로부터 5일 뒤 정산 완료된다고 가정 -> 현재로부터 5일 전 결제가 진행된 정산 내역 확인
            response = tossSettlementClient.retrieve(
                    String.valueOf(soldAt.minusDays(1).toLocalDate()), String.valueOf(soldAt.toLocalDate()));
        } catch (Exception e) {
            log.warn("TOSS API 호출 실패 - 원인: {}", e.getMessage());
            throw new BusinessException(ExceptionCode.SERVICE_UNAVAILABLE);
        }

        log.info("TOSS API 호출 성공 -> 정산 확인");
        for (TossSettlementResult result : response) {
            String orderId = result.getOrderId();

            Settlement settlement = settlementMap.get(orderId);
            if (settlement != null) {
                settlement.settle(result.getAmount(), result.getFee(), result.getPayOutAmount());
                settlementIds.add(settlement.getId());
            }
        }

        log.info("정산 완료 개수: {}", settlementIds.size());
    }
}
