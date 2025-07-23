package com.purchase.preorder.user_service.wishlist_item;

import com.common.core.exception.ExceptionCode;
import com.common.domain.entity.Wishlist;
import com.common.domain.entity.WishlistItem;
import com.common.domain.repository.WishlistItemRepository;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.user_service.client.ItemClient;
import com.purchase.preorder.user_service.client.ItemResponse;
import com.purchase.preorder.user_service.wishlist.dto.ResWishListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistItemService {
    private final WishlistItemRepository wishListItemRepository;
    private final ItemClient itemClient;

    public void createWishListItem(Wishlist wishlist, List<Long> itemIds) {
        Set<Long> existingItemIds = wishlist.getWishlistItems().stream()
                .map(WishlistItem::getItemId)
                .collect(Collectors.toSet());

        List<WishlistItem> wishlistItems = itemIds.stream()
                .filter(itemId -> !existingItemIds.contains(itemId))
                .map(WishlistItem::of)
                .toList();

        wishlistItems.forEach(wishlist::addWishlistItem); // 연관관계 설정
        wishListItemRepository.saveAll(wishlistItems);
    }


    public void deleteWishListItem(Wishlist wishlist, List<Long> itemIds) {
        List<WishlistItem> wishlistItems = wishListItemRepository.findAllByWishlistAndItemIdIn(wishlist, itemIds);

        if (wishlistItems.isEmpty()) {
            throw new BusinessException(ExceptionCode.NOT_FOUND_WISHLIST_ITEM);
        }

        wishListItemRepository.deleteAllInBatch(wishlistItems);
    }

    public List<ResWishListItemDto> fromEntities(List<WishlistItem> wishlistItems) {
        List<Long> itemIds = wishlistItems.stream().map(WishlistItem::getItemId).toList();
        List<ItemResponse> items = itemClient.getItems(itemIds);

        return items.stream()
                .map(ResWishListItemDto::from)
                .toList();
    }
}
