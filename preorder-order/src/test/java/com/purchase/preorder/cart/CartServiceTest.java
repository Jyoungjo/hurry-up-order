package com.purchase.preorder.cart;

import com.purchase.preorder.cart.dto.ReqCartDto;
import com.purchase.preorder.cart.dto.ResCartDto;
import com.purchase.preorder.cart.dto.ResCartItemDto;
import com.purchase.preorder.cart_item.CartItemService;
import com.purchase.preorder.client.UserClient;
import com.purchase.preorder.client.response.UserResponse;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.purchase.preorder.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private UserClient userClient;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private UserResponse user;
    private static MockedStatic<JwtParser> jwtParser;
    private static MockedStatic<CustomCookieManager> cookieManager;

    @BeforeEach
    void init() {
        user = new UserResponse(
                1L, "이름", "a3acfa0a0267531ddd493ead683a99ae",
                LocalDateTime.now(), "12345", "주소",
                "010-1234-1234", "CERTIFIED_USER", null
        );

        cart = Cart.builder()
                .id(1L)
                .userId(1L)
                .cartItems(new ArrayList<>())
                .build();

        AesUtils aesUtils = new AesUtils();
        aesUtils.setPrivateKey("qwe123asd456zxc789q7a4z1w8s5x288");

        JwtParser jwtParser = new JwtParser();
        jwtParser.setKey("abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789");
    }

    @BeforeAll
    static void beforeAll() {
        jwtParser = mockStatic(JwtParser.class);
        cookieManager = mockStatic(CustomCookieManager.class);
    }

    @AfterAll
    static void afterAll() {
        jwtParser.close();
        cookieManager.close();
    }

    // CREATE
    @DisplayName("장바구니 추가 기능 성공")
    @Test
    void addItem() throws Exception {
        // given
        ReqCartDto req = new ReqCartDto(1L, 5);

        MockHttpServletRequest request = new MockHttpServletRequest();
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        doNothing().when(cartItemService).createCartItem(any(Cart.class), any(ReqCartDto.class));

        // when
        cartService.addItemToCart(request, req);

        // then
        verify(userClient, times(1)).getUserByEmail(email);
        verify(cartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemService, times(1)).createCartItem(cart, req);
    }

    // CREATE
    @DisplayName("장바구니 추가 기능 실패 - 존재하지 않는 유저")
    @Test
    void addItemFailNotFound() {
        // given
        ReqCartDto req = new ReqCartDto(1L, 5);

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // when

        // then
        assertThatThrownBy(() -> cartService.addItemToCart(request, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("장바구니 조회 기능 성공")
    @Test
    void readMyCart() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(cartItemService.fromEntities(any())).thenReturn(List.of(
                new ResCartItemDto(1L, "제품명", 10000, 500)
        ));

        // when
        ResCartDto res = cartService.readMyCart(request);

        // then
        assertThat(res.getCartId()).isEqualTo(cart.getId());
    }

    // READ
    @DisplayName("장바구니 조회 기능 실패 - 존재하지 않는 유저")
    @Test
    void readMyCartFailNotFoundUser() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // when

        // then
        assertThatThrownBy(() -> cartService.readMyCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("장바구니 조회 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void readMyCartFailNotFoundCart() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.readMyCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 추가 기능 성공")
    @Test
    void incrementCartItemQuantity() throws Exception {
        // given
        Long itemId = 1L;
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        doNothing().when(cartItemService).incrementCartItem(any(Cart.class), anyLong());

        // when
        cartService.incrementCartItemQuantity(request, itemId);

        // then
        verify(userClient, times(1)).getUserByEmail(email);
        verify(cartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemService, times(1)).incrementCartItem(cart, itemId);
    }

    // UPDATE
    @DisplayName("장바구니 수량 추가 기능 실패 - 존재하지 않는 유저")
    @Test
    void incrementCartItemQuantityFailNotFoundUser() {
        // given
        Long itemId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenThrow(new BusinessException(NOT_FOUND_USER));
        // when

        // then
        assertThatThrownBy(() -> cartService.incrementCartItemQuantity(request, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 추가 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void incrementCartItemQuantityFailNotFoundCart() {
        // given
        Long itemId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.incrementCartItemQuantity(request, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 감소 기능 성공")
    @Test
    void decrementCartItemQuantity() throws Exception {
        // given
        Long itemId = 1L;
        String email = "a3acfa0a0267531ddd493ead683a99ae";
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        doNothing().when(cartItemService).decrementCartItem(any(Cart.class), anyLong());

        // when
        cartService.decrementCartItemQuantity(request, itemId);

        // then
        verify(userClient, times(1)).getUserByEmail(email);
        verify(cartRepository, times(1)).findByUserId(user.getId());
        verify(cartItemService, times(1)).decrementCartItem(cart, itemId);
    }

    // UPDATE
    @DisplayName("장바구니 수량 감소 기능 실패 - 존재하지 않는 유저")
    @Test
    void decrementCartItemQuantityFailNotFoundUser() {
        // given
        Long itemId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // when

        // then
        assertThatThrownBy(() -> cartService.decrementCartItemQuantity(request, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE
    @DisplayName("장바구니 수량 감소 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void decrementCartItemQuantityFailNotFoundCart() {
        // given
        Long itemId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.decrementCartItemQuantity(request, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 성공")
    @Test
    void clearCart() throws Exception {
        // given
        String email = "a3acfa0a0267531ddd493ead683a99ae";
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);
        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // when
        cartService.clearCart(request);

        // then
        verify(userClient, times(1)).getUserByEmail(email);
        verify(cartRepository, times(1)).findByUserId(user.getId());
        verify(cartRepository, times(1)).save(cart);
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 실패 - 존재하지 않는 유저")
    @Test
    void clearCartFailNotFoundUser() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // when

        // then
        assertThatThrownBy(() -> cartService.clearCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 실패 - 존재하지 않는 장바구니")
    @Test
    void clearCartFailNotFoundCart() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cartService.clearCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    // DELETE
    @DisplayName("장바구니 비우기 기능 실패 - 이메일 불일치")
    @Test
    void clearCartFailNotMatch() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test1@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));

        // when

        // then
        assertThatThrownBy(() -> cartService.clearCart(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }
}
