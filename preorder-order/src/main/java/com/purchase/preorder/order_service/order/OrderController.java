package com.purchase.preorder.order_service.order;

import com.purchase.preorder.order_service.order.dto.ReqCancelOrderDto;
import com.purchase.preorder.order_service.order.dto.ReqOrderDto;
import com.purchase.preorder.order_service.order.dto.ResOrderDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ResOrderDto> createOrder(
            @RequestBody ReqOrderDto req, HttpServletRequest request
    ) throws Exception {
        return ResponseEntity.status(CREATED).body(orderService.createOrder(req, request));
    }

    @GetMapping
    public ResponseEntity<Page<ResOrderDto>> readAllOrder(
            HttpServletRequest request, @RequestParam("page") Integer page, @RequestParam("size") Integer size
    ) throws Exception {
        return ResponseEntity.ok(orderService.readAllOrder(request, page, size));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResOrderDto> readOrder(
            HttpServletRequest request, @PathVariable("orderId") Long orderId
    ) throws Exception {
        return ResponseEntity.ok(orderService.readOrder(request, orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            HttpServletRequest request, @RequestBody ReqCancelOrderDto req, @PathVariable("orderId") Long orderId
    ) throws Exception {
        orderService.cancelOrder(request, orderId, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}/return")
    public ResponseEntity<Void> returnOrder(
            HttpServletRequest request, @RequestBody List<Long> orderItemIds, @PathVariable("orderId") Long orderId
    ) throws Exception {
        orderService.returnOrder(request, orderId, orderItemIds);
        return ResponseEntity.noContent().build();
    }
}
