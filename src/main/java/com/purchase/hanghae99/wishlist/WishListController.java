package com.purchase.hanghae99.wishlist;

import com.purchase.hanghae99.wishlist.dto.ResWishListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wishlist")
public class WishListController {
    private final WishListService wishListService;

    @PostMapping
    public ResponseEntity<Long> createWishList(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishListService.createWishList(authentication));
    }

    @PostMapping("/{wishListId}")
    public ResponseEntity<Void> addItemToWishList(
            @PathVariable("wishListId") Long wishListId,
            @RequestParam("itemId") Long itemId
    ) {
        wishListService.addItemToWishList(wishListId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{wishListId}")
    public ResponseEntity<Void> removeItemFromWishList(
            @PathVariable("wishListId") Long wishListId,
            @RequestParam("itemId") Long itemId
    ) {
        wishListService.removeItemFromWishList(wishListId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{wishListId}")
    public ResponseEntity<ResWishListDto> readWishList(
            Authentication authentication, @PathVariable("wishListId") Long wishListId
    ) {
        return ResponseEntity.ok(wishListService.readWishList(authentication, wishListId));
    }
}
