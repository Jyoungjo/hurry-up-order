package com.purchase.preorder.shipment_service.shipment.scheduler;

import com.common.domain.common.ShipmentStatus;
import com.common.domain.entity.order.Shipment;
import com.common.domain.repository.order.ShipmentRepository;
import com.common.event_common.domain_event_vo.shipment.ShipmentReturnedDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentStatusChangedDomainEvent;
import com.common.event_common.mapper.ShipmentDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.common.domain.common.ShipmentStatus.*;

@Slf4j
@Component
public class ShipmentScheduler {

    private final ShipmentRepository shipmentRepository;
    private final DomainEventPublisher publisher;
    private final ShipmentDomainEventMapper mapper;

    public ShipmentScheduler(ShipmentRepository shipmentRepository,
                             @Qualifier("shipmentDomainEventPublisher") DomainEventPublisher publisher,
                             ShipmentDomainEventMapper mapper) {
        this.shipmentRepository = shipmentRepository;
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Scheduled(cron = "0 */30 * * * ?") // 30분 간격
    @Transactional
    public void updateToReady() {
        updateStatus(ACCEPTANCE, READY, LocalDateTime.now().minusMinutes(30));
    }

    @Scheduled(cron = "0 0 * * * ?") // 1시간 간격
    @Transactional
    public void updateToShipping() {
        updateStatus(READY, SHIPPING, LocalDateTime.now().minusDays(1));
    }

    @Scheduled(cron = "0 0 * * * ?") // 1시간 간격
    @Transactional
    public void updateToDelivered() {
        updateStatus(SHIPPING, SHIPPED, LocalDateTime.now().minusDays(1));
    }

    @Scheduled(cron = "0 */10 * * * ?") // 10분마다
    @Transactional
    public void processReturns() {
        processReturnedShipments(LocalDateTime.now());
    }

    private void updateStatus(ShipmentStatus from, ShipmentStatus to, LocalDateTime threshold) {
        List<Shipment> shipments = shipmentRepository.findAllByStatus(from).stream()
                .filter(shipment -> shipment.getCreatedAt().isBefore(threshold))
                .toList();

        if (!shipments.isEmpty()) {
            shipmentRepository.updateStatusByOrderItemIds(to, shipments.stream().map(Shipment::getOrderItemId).toList());

            for (Shipment shipment : shipments) {
                ShipmentStatusChangedDomainEvent event = mapper.toShipmentStatusChangedEvent(
                        shipment.getId(), shipment.getStatus().name());

                publisher.publishOnlySpringEventAfterCommit(event);
            }
        }

        log.info("배송 상태 업데이트: {} → {}, count: {}", from, to, shipments.size());
    }

    private void processReturnedShipments(LocalDateTime now) {
        List<Shipment> shipments = shipmentRepository.findAllByStatus(RETURN_REQUESTED).stream()
                .filter(shipment -> shipment.getUpdatedAt().plusDays(1).isBefore(now))
                .toList();

        if (!shipments.isEmpty()) {
            shipmentRepository.updateStatusByOrderItemIds(RETURNED, shipments.stream().map(Shipment::getOrderItemId).toList());

            for (Shipment shipment : shipments) {
                ShipmentReturnedDomainEvent event = mapper.toShipmentReturnedEvent(shipment.getId(), shipment.getOrderItemId());
                publisher.publishOnlySpringEventAfterCommit(event);
            }
        }

        log.info("배송 상태 업데이트: {} → {}, count: {}", RETURN_REQUESTED, RETURNED, shipments.size());
    }
}
