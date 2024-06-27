package com.purchase.hanghae99.cart;

import com.purchase.hanghae99.cart.dto.ReqCartDto;
import com.purchase.hanghae99.cart.dto.ResCartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            Authentication authentication, @RequestBody ReqCartDto req
    ) throws Exception {
        cartService.addItemToCart(authentication, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResCartDto> readMyCart(Authentication authentication) throws Exception {
        return ResponseEntity.ok(cartService.readMyCart(authentication));
    }

    @PutMapping("/{itemId}/increase")
    public ResponseEntity<Void> incrementCartItemQuantity(
            Authentication authentication, @PathVariable("itemId") Long itemId
    ) throws Exception {
        cartService.incrementCartItemQuantity(authentication, itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{itemId}/decrease")
    public ResponseEntity<Void> decrementCartItemQuantity(
            Authentication authentication, @PathVariable("itemId") Long itemId
    ) throws Exception {
        cartService.decrementCartItemQuantity(authentication, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) throws Exception {
        cartService.clearCart(authentication);
        return ResponseEntity.noContent().build();
    }
}