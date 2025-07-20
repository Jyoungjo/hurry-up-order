package com.purchase.preorder.user_service.wishlist;

import com.purchase.preorder.user_service.wishlist.dto.ResWishListDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service/api/v1/wishlists")
public class WishlistController {
    private final WishlistService wishListService;

    @PostMapping("/wishlist/items")
    public ResponseEntity<Void> addItemToWishList(
            HttpServletRequest request, @RequestBody List<Long> itemIds
    ) throws Exception {
        wishListService.addItemToWishList(request, itemIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/wishlist/items")
    public ResponseEntity<Void> removeItemFromWishList(
            HttpServletRequest request, @RequestBody List<Long> itemIds
    ) throws Exception {
        wishListService.removeItemFromWishList(request, itemIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResWishListDto> readWishList(
            HttpServletRequest request
    ) throws Exception {
        return ResponseEntity.ok(wishListService.readMyWishList(request));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearWishlist(HttpServletRequest request) throws Exception {
        wishListService.clearWishlist(request);
        return ResponseEntity.noContent().build();
    }
}
