package com.purchase.hanghae99.order;

import com.purchase.hanghae99.order.dto.ReqOrderDto;
import com.purchase.hanghae99.order.dto.ResOrderDto;
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
    ) {
        return ResponseEntity.status(CREATED).body(orderService.createOrder(req, authentication));
    }

    @GetMapping
    public ResponseEntity<Page<ResOrderDto>> readAllOrder(
            Authentication authentication, @RequestParam("page") Integer page, @RequestParam("size") Integer size
    ) {
        return ResponseEntity.ok(orderService.readAllOrder(authentication, page, size));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResOrderDto> readOrder(
            Authentication authentication, @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(orderService.readOrder(authentication, orderId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ResOrderDto> updateOrder(
            Authentication authentication, @PathVariable("orderId") Long orderId, @RequestBody ReqOrderDto req
    ) {
        return ResponseEntity.ok(orderService.updateOrder(authentication, orderId, req));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            Authentication authentication, @PathVariable("orderId") Long orderId
    ) {
        orderService.deleteOrder(authentication, orderId);
        return ResponseEntity.noContent().build();
    }
}
