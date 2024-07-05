package com.purchase.preorder.wishlist_item;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.exception.ExceptionCode;
import com.purchase.preorder.client.ItemClient;
import com.purchase.preorder.client.ItemResponse;
import com.purchase.preorder.wishlist.Wishlist;
import com.purchase.preorder.wishlist.dto.ResWishListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistItemService {
    private final WishlistItemRepository wishListItemRepository;
    private final ItemClient itemClient;

    public void createWishListItem(Wishlist wishlist, Long itemId) {
        wishlist.getWishlistItems().stream()
                .filter(wishlistItem -> wishlistItem.getItemId().equals(itemId))
                .findFirst()
                .ifPresentOrElse(
                        wishlistItem -> {
                            throw new BusinessException(ExceptionCode.ALREADY_EXISTS_ITEM);
                        }, () -> {
                            WishlistItem newWishlistItem = WishlistItem.of(itemId, wishlist);
                            wishListItemRepository.save(newWishlistItem);
                        }
                );
    }

    public void deleteWishListItem(Wishlist wishlist, Long itemId) {
        WishlistItem wishlistItem = wishListItemRepository.findByWishlistAndItemId(wishlist, itemId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_WISHLIST_ITEM));

        wishListItemRepository.delete(wishlistItem);
    }

    public ResWishListItemDto fromEntity(WishlistItem wishlistItem) {
        ItemResponse itemResponse = itemClient.getItem(wishlistItem.getItemId());

        return ResWishListItemDto.builder()
                .itemId(wishlistItem.getItemId())
                .name(itemResponse.getName())
                .price(itemResponse.getPrice())
                .build();
    }

    public List<ResWishListItemDto> fromEntities(List<WishlistItem> wishlistItems) {
        return wishlistItems.stream()
                .map(this::fromEntity)
                .toList();
    }
}
