package com.purchase.preorder.cart_service.cart.controller;

import com.purchase.preorder.cart_service.cart.dto.ReqCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartDto;
import com.purchase.preorder.cart_service.cart.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service/api/v1/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            HttpServletRequest request, @RequestBody List<ReqCartDto> req
    ) throws Exception {
        cartService.addItemToCart(request, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResCartDto> readMyCart(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(cartService.readMyCart(request));
    }

    @PutMapping("/{itemId}/increase")
    public ResponseEntity<Void> incrementCartItemQuantity(
            HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody int amount
    ) throws Exception {
        cartService.incrementCartItemQuantity(request, itemId, amount);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{itemId}/decrease")
    public ResponseEntity<Void> decrementCartItemQuantity(
            HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody int amount
    ) throws Exception {
        cartService.decrementCartItemQuantity(request, itemId, amount);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(HttpServletRequest request) throws Exception {
        cartService.clearCart(request);
        return ResponseEntity.noContent().build();
    }
}
