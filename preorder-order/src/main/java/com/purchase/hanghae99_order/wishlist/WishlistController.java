package com.purchase.hanghae99_order.wishlist;

import com.purchase.hanghae99_order.wishlist.dto.ResWishListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wishlists")
public class WishlistController {
    private final WishlistService wishListService;

    @PostMapping
    public ResponseEntity<Void> addItemToWishList(
            Authentication authentication,
            @RequestParam("itemId") Long itemId
    ) throws Exception {
        wishListService.addItemToWishList(authentication, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeItemFromWishList(
            Authentication authentication,
            @RequestParam("itemId") Long itemId
    ) throws Exception {
        wishListService.removeItemFromWishList(authentication, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResWishListDto> readWishList(
            Authentication authentication
    ) throws Exception {
        return ResponseEntity.ok(wishListService.readMyWishList(authentication));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearWishlist(Authentication authentication) throws Exception {
        wishListService.clearWishlist(authentication);
        return ResponseEntity.noContent().build();
    }
}
