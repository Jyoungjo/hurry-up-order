package com.purchase.hanghae99.cart;

import com.purchase.hanghae99.cart.dto.ReqCartDto;
import com.purchase.hanghae99.cart.dto.ResCartDto;
import com.purchase.hanghae99.cart_item.CartItemService;
import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    @Override
    @Transactional
    public void addItemToCart(Authentication authentication, ReqCartDto req) {
        String emailOfConnectingUser = authentication.getName();

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.of(user);
                    return cartRepository.save(newCart);
                });

        cartItemService.createCartItem(cart, req);
    }

    @Override
    public ResCartDto readMyCart(Authentication authentication) {
        String emailOfConnectingUser = authentication.getName();

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        return ResCartDto.fromEntity(cart);
    }

    @Override
    @Transactional
    public void incrementCartItemQuantity(Authentication authentication, Long itemId) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cartItemService.incrementCartItem(cart, itemId);
    }

    @Override
    @Transactional
    public void decrementCartItemQuantity(Authentication authentication, Long itemId) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cartItemService.decrementCartItem(cart, itemId);
    }

    @Override
    @Transactional
    public void clearCart(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        checkAccount(cart.getUser().getEmail(), authentication.getName());

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private void checkAccount(String email, String myEmail) {
        if (!email.equals(myEmail)) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }
}
