package com.purchase.preorder.cart_service.cart.service;

import com.common.core.util.JwtParser;
import com.common.domain.entity.order.Cart;
import com.common.domain.repository.order.CartRepository;
import com.common.web.auth.JwtUtils;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.cart_service.cart.dto.ReqCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartDto;
import com.purchase.preorder.cart_service.cart.dto.ResCartItemDto;
import com.purchase.preorder.cart_service.cart_item.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.common.core.exception.ExceptionCode.NOT_FOUND_CART;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void addItemToCart(HttpServletRequest request, List<ReqCartDto> req) {
        Long userId = getUserIdFromAuthentication(request);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.of(userId)));

        cartItemService.createCartItem(cart, req);
    }

    @Override
    public ResCartDto readMyCart(HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(request);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        List<ResCartItemDto> resCartItemDtoList = cartItemService.fromEntities(cart.getCartItems());

        return ResCartDto.fromEntity(cart, resCartItemDtoList);
    }

    @Override
    @Transactional
    public void incrementCartItemQuantity(HttpServletRequest request, Long itemId, int amount) {
        Long userId = getUserIdFromAuthentication(request);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cartItemService.incrementCartItem(cart, itemId, amount);
    }

    @Override
    @Transactional
    public void decrementCartItemQuantity(HttpServletRequest request, Long itemId, int amount) {
        Long userId = getUserIdFromAuthentication(request);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cartItemService.decrementCartItem(cart, itemId, amount);
    }

    @Override
    @Transactional
    public void clearCart(HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(request);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cart.getCartItems().clear();
    }

    @Override
    public void delete(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    private Long getUserIdFromAuthentication(HttpServletRequest request) {
        String accessToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.AUTHORIZATION));
        return JwtParser.getUserId(accessToken);
    }
}
