package com.purchase.hanghae99_item.order;

import com.purchase.hanghae99_item.order.dto.ReqOrderDto;
import com.purchase.hanghae99_item.order.dto.ResOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ResOrderDto> createOrder(
            @RequestBody ReqOrderDto req, Authentication authentication
    ) throws Exception {
        return ResponseEntity.status(CREATED).body(orderService.createOrder(req, authentication));
    }

    @GetMapping
    public ResponseEntity<Page<ResOrderDto>> readAllOrder(
            Authentication authentication, @RequestParam("page") Integer page, @RequestParam("size") Integer size
    ) throws Exception {
        return ResponseEntity.ok(orderService.readAllOrder(authentication, page, size));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResOrderDto> readOrder(
            Authentication authentication, @PathVariable("orderId") Long orderId
    ) throws Exception {
        return ResponseEntity.ok(orderService.readOrder(authentication, orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            Authentication authentication, @RequestParam("itemId") Long itemId, @PathVariable("orderId") Long orderId
    ) throws Exception {
        orderService.cancelOrder(authentication, orderId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}/return")
    public ResponseEntity<Void> returnOrder(
            Authentication authentication, @RequestParam("itemId") Long itemId, @PathVariable("orderId") Long orderId
    ) throws Exception {
        orderService.returnOrder(authentication, orderId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            Authentication authentication, @PathVariable("orderId") Long orderId
    ) throws Exception {
        orderService.deleteOrder(authentication, orderId);
        return ResponseEntity.noContent().build();
    }
}
