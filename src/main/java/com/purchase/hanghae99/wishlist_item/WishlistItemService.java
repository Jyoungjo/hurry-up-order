package com.purchase.hanghae99.wishlist_item;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.common.exception.ExceptionCode;
import com.purchase.hanghae99.item.Item;
import com.purchase.hanghae99.item.ItemService;
import com.purchase.hanghae99.wishlist.Wishlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistItemService {
    private final WishlistItemRepository wishListItemRepository;
    private final ItemService itemService;

    public void createWishListItem(Wishlist wishlist, Long itemId) {
        Item item = itemService.findItem(itemId);

        wishlist.getWishlistItems().stream()
                .filter(wishlistItem -> wishlistItem.getItem().getId().equals(itemId))
                .findFirst()
                .ifPresentOrElse(
                        wishlistItem -> {
                            throw new BusinessException(ExceptionCode.ALREADY_EXISTS_ITEM);
                        }, () -> {
                            WishlistItem newWishlistItem = WishlistItem.of(item, wishlist);
                            wishListItemRepository.save(newWishlistItem);
                        }
                );
    }

    public void deleteWishListItem(Wishlist wishlist, Long itemId) {
        WishlistItem wishlistItem = wishListItemRepository.findByWishListAndItemId(wishlist, itemId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_WISHLIST_ITEM));

        wishListItemRepository.delete(wishlistItem);
    }
}
