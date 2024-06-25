package com.purchase.hanghae99.wishlist_items;

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
public class WishListItemService {
    private final WishListItemRepository wishListItemRepository;
    private final ItemService itemService;

    @Transactional
    public void createWishListItem(Wishlist wishlist, Long itemId) {
        Item item = itemService.findItem(itemId);
        wishListItemRepository.save(WishlistItem.of(item, wishlist));
    }

    @Transactional
    public void deleteWishListItem(Wishlist wishlist, Long itemId) {
        WishlistItem wishlistItem = wishListItemRepository.findByWishListAndItemId(wishlist, itemId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_WISHLIST_ITEM));

        wishListItemRepository.delete(wishlistItem);
    }
}
