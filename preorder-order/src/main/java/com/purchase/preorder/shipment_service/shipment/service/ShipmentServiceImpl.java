package com.purchase.preorder.shipment_service.shipment.service;

import com.common.domain.entity.order.Shipment;
import com.common.domain.repository.order.ShipmentRepository;
import com.common.event_common.domain_event_vo.shipment.ShipmentCanceledDomainEvent;
import com.common.event_common.domain_event_vo.shipment.ShipmentCreatedDomainEvent;
import com.common.event_common.mapper.ShipmentDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import com.purchase.preorder.shipment_service.dto.ResShipmentCreatedDto;
import com.purchase.preorder.shipment_service.shipment.repository.ShipmentJDBCRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.common.domain.common.ShipmentStatus.*;

@Slf4j
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentJDBCRepository jdbcRepository;
    private final ShipmentDomainEventMapper mapper;
    private final DomainEventPublisher publisher;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository,
                               ShipmentJDBCRepository jdbcRepository,
                               ShipmentDomainEventMapper mapper,
                               @Qualifier("shipmentDomainEventPublisher") DomainEventPublisher publisher) {
        this.shipmentRepository = shipmentRepository;
        this.jdbcRepository = jdbcRepository;
        this.mapper = mapper;
        this.publisher = publisher;
    }

    /*
     * 단건 생성 메서드
     */
    @Override
    public ResShipmentCreatedDto createShipment(Long orderItemId, Long orderId) {
        return createShipments(List.of(orderItemId), orderId).getFirst();
    }

    /*
     * 벌크 insert
     * 이벤트로 인한 생성 및 저장이 이루어지기 때문에 데이터 정합성 유지 필요
     */
    @Override
    @Transactional
    public List<ResShipmentCreatedDto> createShipments(List<Long> orderItemIds, Long orderId) {
        List<Shipment> shipments = orderItemIds.stream()
                .map(Shipment::of)
                .toList();

        List<Long> savedShipmentIds = jdbcRepository.saveAll(shipments);
        log.info("배송 정보 저장 완료 - 갯수: {}", savedShipmentIds.size());

        Map<Long, Long> shipmentMap = IntStream.range(0, orderItemIds.size())
                .boxed()
                .collect(Collectors.toMap(
                        orderItemIds::get,
                        savedShipmentIds::get
                ));

        ShipmentCreatedDomainEvent event = mapper.toShipmentCreatedEvent(shipmentMap, orderId);
        publisher.publishOnlySpringEventAfterCommit(event);

        return shipments.stream()
                .map(s -> ResShipmentCreatedDto.of(s.getId(), s.getOrderItemId()))
                .toList();
    }

    @Override
    @Transactional
    public void cancelShipments(Long orderId, List<Long> orderItemIds) {
        shipmentRepository.updateStatusByOrderItemIds(CANCELED, orderItemIds);

        List<Shipment> shipments = shipmentRepository.findAllByOrderItemIdIn(orderItemIds);

        for (Shipment shipment : shipments) {
            ShipmentCanceledDomainEvent event = mapper.toShipmentCanceledEvent(shipment.getId(), orderId);
            publisher.publishOnlySpringEventAfterCommit(event);
        }
    }

    @Override
    @Transactional
    public void requestReturnShipments(Long orderId, List<Long> orderItemIds) {
        List<Shipment> shipments = shipmentRepository.findAllByOrderItemIdIn(orderItemIds);
        shipments.forEach(shipment -> shipment.updateStatus(RETURN_REQUESTED));
    }

    @Override
    public void delete(List<Long> orderItemIds) {
        shipmentRepository.deleteAllByOrderItemIdsIn(LocalDateTime.now(), orderItemIds);
    }

    @Override
    public void deleteAllByOrderItemId(List<Long> orderItemIds) {
        shipmentRepository.deleteAllByOrderItemIdsIn(LocalDateTime.now(), orderItemIds);
    }

    // TODO 서비스 분리시 Controller 에서 API 만들어야 함
    @Override
    public boolean validateCancelable(List<Long> orderItemIds) {
        return shipmentRepository.findAllByOrderItemIdIn(orderItemIds).stream()
                .allMatch(shipment -> shipment.getStatus() == ACCEPTANCE || shipment.getStatus() == READY);
    }

    // TODO 서비스 분리시 Controller 에서 API 만들어야 함
    @Override
    public boolean validateReturnable(List<Long> orderItemIds) {
        return shipmentRepository.findAllByOrderItemIdIn(orderItemIds).stream()
                .allMatch(shipment -> shipment.getStatus() == SHIPPED || shipment.getCreatedAt().plusDays(1).isBefore(LocalDateTime.now()));
    }
}
