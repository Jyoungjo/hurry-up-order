package com.purchase.preorder.order_service_common.log;

import com.common.domain.entity.common.ProcessedEventLog;
import com.common.domain.repository.common.ProcessedEventRepository;
import com.common.kafka.log.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProcessedEventServiceImpl implements ProcessedEventService {

    private final ProcessedEventRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean isProcessed(String eventId) {
        if (repository.existsById(eventId)) {
            log.info("중복 이벤트 소비 확인 - eventId: {}", eventId);
            return true;
        }

        repository.save(ProcessedEventLog.from(eventId));
        return false;
    }
}
