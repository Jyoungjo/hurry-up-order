package com.purchase.preorder.order_service.cart;

import com.purchase.preorder.order_service.cart.dto.ResCartItemDto;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.exception.ExceptionCode;
import com.purchase.preorder.order_service.cart_item.CartItemService;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import com.purchase.preorder.order_service.cart.dto.ReqCartDto;
import com.purchase.preorder.order_service.cart.dto.ResCartDto;
import com.purchase.preorder.order_service.client.UserClient;
import com.purchase.preorder.order_service.client.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final UserClient userClient;
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    @Override
    @Transactional
    public void addItemToCart(HttpServletRequest request, ReqCartDto req) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.of(user.getId());
                    return cartRepository.save(newCart);
                });

        cartItemService.createCartItem(cart, req);
    }

    @Override
    public ResCartDto readMyCart(HttpServletRequest request) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_CART));

        List<ResCartItemDto> resCartItemDtoList = cartItemService.fromEntities(cart.getCartItems());

        return ResCartDto.fromEntity(cart, resCartItemDtoList);
    }

    @Override
    @Transactional
    public void incrementCartItemQuantity(HttpServletRequest request, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_CART));

        cartItemService.incrementCartItem(cart, itemId);
    }

    @Override
    @Transactional
    public void decrementCartItemQuantity(HttpServletRequest request, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_CART));

        cartItemService.decrementCartItem(cart, itemId);
    }

    @Override
    @Transactional
    public void clearCart(HttpServletRequest request) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        UserResponse user = userClient.getUserByEmail(emailOfConnectingUser);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_CART));

        checkAccount(user.getEmail(), emailOfConnectingUser);

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private void checkAccount(String email, String myEmail) {
        if (!email.equals(myEmail)) {
            throw new BusinessException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }
    }

    private String getEmailOfAuthenticatedUser(HttpServletRequest request) throws Exception {
        String accessToken = CustomCookieManager.getCookie(request, CustomCookieManager.ACCESS_TOKEN);
        return AesUtils.aesCBCEncode(JwtParser.getEmail(accessToken));
    }
}
