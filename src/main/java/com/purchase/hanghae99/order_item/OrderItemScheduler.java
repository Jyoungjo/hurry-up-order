package com.purchase.hanghae99.order_item;

import com.purchase.hanghae99.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemScheduler {
    private final OrderItemRepository orderItemRepository;
    private final StockService stockService;

    @Scheduled(cron = "0 0 * * * ?") // 매 시간 실행
    public void updateOrderStatusWithScheduler() {
        LocalDateTime now = LocalDateTime.now();

        // 주문 접수된 상태에서 1시간 지난 주문을 배송 준비로 변경
        List<OrderItem> acceptanceOrders = orderItemRepository.findAllByStatus(OrderStatus.ACCEPTANCE);

        acceptanceOrders.stream()
                .filter(orderItem -> orderItem.getAcceptedAt().plusHours(1).isBefore(now))
                .forEach(orderItem -> {
                    orderItem.updateStatus(OrderStatus.READY);
                    orderItemRepository.save(orderItem);
                });

        // 배송 준비 상태에서 하루 지난 주문을 배송 중으로 변경
        List<OrderItem> readyOrders = orderItemRepository.findAllByStatus(OrderStatus.READY);

        readyOrders.stream()
                .filter(orderItem -> orderItem.getReadyAt().plusDays(1).isBefore(now))
                .forEach(orderItem -> {
                    orderItem.updateStatus(OrderStatus.SHIPPING);
                    orderItemRepository.save(orderItem);
                });

        // 배송 중 상태에서 하루 지난 주문을 배송 완료로 변경
        List<OrderItem> shippingOrders = orderItemRepository.findAllByStatus(OrderStatus.SHIPPING);

        shippingOrders.stream()
                .filter(orderItem -> orderItem.getShippingAt().plusDays(1).isBefore(now))
                .forEach(orderItem -> {
                    orderItem.updateStatus(OrderStatus.DELIVERED);
                    orderItemRepository.save(orderItem);
                });

        // 반품 신청한 상태에서 하루 지난 주문을 반품 완료로 변경 후, 재고 복구
        List<OrderItem> requestReturnOrders = orderItemRepository.findAllByStatus(OrderStatus.REQUEST_RETURN);

        requestReturnOrders.stream()
                .filter(orderItem -> orderItem.getRequestReturnAt().plusDays(1).isBefore(now))
                .forEach(orderItem -> {
                    orderItem.updateStatus(OrderStatus.RETURNED);
                    orderItemRepository.save(orderItem);
                    stockService.increaseStock(orderItem.getItem(), orderItem.getQuantity());
                });
    }
}
