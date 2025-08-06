package com.purchase.preorder.settlement_service.settlement.repository;

import com.purchase.preorder.settlement_service.api.external.pg.dto.TossSettlementResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SettlementJDBCRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void checkSettlement(Map<String, Long> pgToId, List<TossSettlementResult> results) {
        String sql = """
                    UPDATE tb_settlement
                       SET settled_at       = ?
                         , settlement_amount = ?
                         , fee               = ?
                         , pay_out_amount    = ?
                    WHERE id = ?
                """;

        LocalDateTime now = LocalDateTime.now();
        List<Object[]> batchParams = results.stream()
                .map(r -> {
                    Long id = pgToId.get(r.getOrderId());
                    if (id != null && r.getCancel() == null) {
                        return new Object[]{
                                Timestamp.valueOf(now),
                                r.getAmount(),
                                r.getFee(),
                                r.getPayOutAmount(),
                                id
                        };
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        // 실제 배치 실행 (배치 사이즈는 자동 분할되거나, 원하는 크기로 직접 나눠도 됩니다)
        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchParams);
        int cnt = Arrays.stream(updateCounts).sum();
        log.info("정산 완료 처리: {}건", cnt);
    }
}
