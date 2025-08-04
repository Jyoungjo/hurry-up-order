package com.purchase.preorder.order_service.shipment;

import com.purchase.preorder.order_service.client.ItemClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.purchase.preorder.shipment.ShipmentStatus.*;

@Component
@RequiredArgsConstructor
public class ShipmentScheduler {
    private final ShipmentRepository shipmentRepository;
    private final ItemClient itemClient;

    @Scheduled(cron = "0 0 * * * ?") // 매 시간 실행
    public void updateOrderStatusByScheduler() {
        LocalDateTime now = LocalDateTime.now();

        // 주문 접수된 상태에서 30분 지난 주문을 배송 준비로 변경
        List<Shipment> acceptanceShipments = shipmentRepository.findAllByStatus(ACCEPTANCE);

        acceptanceShipments.stream()
                .filter(shipment -> shipment.getCreatedAt().plusMinutes(30).isBefore(now))
                .forEach(shipment -> {
                    shipment.updateStatus(READY);
                    shipmentRepository.save(shipment);
                });

        // 배송 준비 상태에서 하루 지난 주문을 배송 중으로 변경
        List<Shipment> readyShipments = shipmentRepository.findAllByStatus(READY);

        readyShipments.stream()
                .filter(shipment -> shipment.getCreatedAt().plusDays(1).isBefore(now))
                .forEach(shipment -> {
                    shipment.updateStatus(SHIPPING);
                    shipmentRepository.save(shipment);
                });

        // 배송 중 상태에서 하루 지난 주문을 배송 완료로 변경
        List<Shipment> shippingShipments = shipmentRepository.findAllByStatus(SHIPPING);

        shippingShipments.stream()
                .filter(shipment -> shipment.getCreatedAt().plusDays(1).isBefore(now))
                .forEach(shipment -> {
                    shipment.updateStatus(DELIVERED);
                    shipmentRepository.save(shipment);
                });

        // 반품 신청한 상태에서 하루 지난 주문을 반품 완료로 변경 후, 재고 복구
        List<Shipment> requestReturnShipments = shipmentRepository.findAllByStatus(REQUEST_RETURN);

        requestReturnShipments.stream()
                .filter(shipment -> shipment.getCreatedAt().plusDays(1).isBefore(now))
                .forEach(shipment -> {
                    shipment.updateStatus(RETURNED);
                    shipmentRepository.save(shipment);
                    itemClient.increaseStock(
                            shipment.getOrderItem().getItemId(), shipment.getOrderItem().getQuantity()
                    );
                });
    }
}
