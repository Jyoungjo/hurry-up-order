package com.purchase.preorder.cart_service.cart_item;

import com.common.domain.entity.order.Cart;
import com.common.domain.entity.order.CartItem;
import com.common.domain.repository.order.CartItemRepository;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.cart_service.cart.dto.ReqCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartItemDto;
import com.purchase.preorder.order_service.api.internal.ItemClient;
import com.purchase.preorder.order_service.api.internal.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.common.core.exception.ExceptionCode.NOT_FOUND_CART_ITEM;


@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ItemClient itemClient;

    public void createCartItem(Cart cart, List<ReqCartDto> req) {
        List<Long> itemIds = req.stream()
                .map(ReqCartDto::getItemId)
                .toList();

        List<CartItem> existingItems = cartItemRepository.findByCartIdAndItemIds(cart.getId(), itemIds);
        Map<Long, CartItem> existingMap = existingItems.stream()
                .collect(Collectors.toMap(CartItem::getItemId, Function.identity()));

        List<CartItem> toSave = new ArrayList<>();

        for (ReqCartDto dto : req) {
            CartItem existing = existingMap.get(dto.getItemId());

            if (existing != null) {
                existing.increaseBy(dto.getQuantity());
                toSave.add(existing);
            } else {
                CartItem cartItem = CartItem.of(dto.getItemId(), dto.getQuantity());
                toSave.add(cartItem);
                cart.addCartItem(cartItem);
            }
        }

        cartItemRepository.saveAll(toSave);
    }

    public void incrementCartItem(Cart cart, Long itemId, int amount) {
        CartItem cartItem = findCartItem(cart, itemId);
        cartItem.increaseBy(amount);
    }

    public void decrementCartItem(Cart cart, Long itemId, int amount) {
        CartItem item = findCartItem(cart, itemId);
        try {
            item.decreaseBy(amount);
        } catch (IllegalArgumentException e) {
            cartItemRepository.delete(item); // 수량 1에서 감소 시 삭제
        }
    }

    @Override
    public List<ResCartItemDto> fromEntities(List<CartItem> cartItems) {
        List<Long> itemIds = cartItems.stream().map(CartItem::getItemId).toList();
        List<ItemResponse> items = itemClient.getItems(itemIds);

        return items.stream()
                .map(i -> ResCartItemDto.of(i.getId(), i.getName(), i.getPrice(), i.getQuantity()))
                .toList();
    }

    private CartItem findCartItem(Cart cart, Long itemId) {
        return cart.getCartItems().stream()
                .filter(c -> c.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART_ITEM));
    }
}
