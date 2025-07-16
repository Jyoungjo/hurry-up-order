package com.purchase.preorder.order_service.cart;

import com.purchase.preorder.order_service.cart.dto.ReqCartDto;
import com.purchase.preorder.order_service.cart.dto.ResCartDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service/api/v1/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            HttpServletRequest request, @RequestBody ReqCartDto req
    ) throws Exception {
        cartService.addItemToCart(request, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResCartDto> readMyCart(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(cartService.readMyCart(request));
    }

    @PutMapping("/increase")
    public ResponseEntity<Void> incrementCartItemQuantity(
            HttpServletRequest request, @RequestParam("itemId") Long itemId
    ) throws Exception {
        cartService.incrementCartItemQuantity(request, itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/decrease")
    public ResponseEntity<Void> decrementCartItemQuantity(
            HttpServletRequest request, @RequestParam("itemId") Long itemId
    ) throws Exception {
        cartService.decrementCartItemQuantity(request, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(HttpServletRequest request) throws Exception {
        cartService.clearCart(request);
        return ResponseEntity.noContent().build();
    }
}
