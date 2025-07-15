package com.purchase.preorder.wishlist;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.user.User;
import com.purchase.preorder.user.UserRepository;
import com.purchase.preorder.user_service.wishlist.WishlistServiceImpl;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import com.purchase.preorder.user_service.wishlist.dto.ResWishListDto;
import com.purchase.preorder.user_service.wishlist_item.WishlistItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static com.purchase.preorder.exception.ExceptionCode.*;
import static com.purchase.preorder.user.UserRole.UNCERTIFIED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class WishlistServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemService wishlistItemService;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    private User user;
    private Wishlist wishlist;
    private static MockedStatic<JwtParser> jwtParser;
    private static MockedStatic<CustomCookieManager> cookieManager;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .email("a3acfa0a0267531ddd493ead683a99ae")
                .role(UNCERTIFIED_USER)
                .name("이름1")
                .phoneNumber("010-1234-5678")
                .address("주소1")
                .deletedAt(null)
                .emailVerifiedAt(null)
                .password("asd1234!!")
                .build();

        wishlist = Wishlist.builder()
                .id(1L)
                .user(user)
                .wishlistItems(new ArrayList<>())
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
    @DisplayName("위시리스트 추가 기능 성공")
    @Test
    void addItemToWishlist() throws Exception {
        // given
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        String email = "a3acfa0a0267531ddd493ead683a99ae";

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));
        doNothing().when(wishlistItemService).createWishListItem(any(Wishlist.class), anyLong());

        // when
        wishlistService.addItemToWishList(request, itemId);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(wishlistRepository, times(1)).findByUser(user);
        verify(wishlistItemService, times(1)).createWishListItem(wishlist, itemId);
    }

    // CREATE
    @DisplayName("위시리스트 추가 기능 실패 - 존재하지 않는 유저")
    @Test
    void addItemToWishlistFailNotFoundUser() {
        // given
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.addItemToWishList(request, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("위시리스트 조회 성공")
    @Test
    void readMyWishlist() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));

        // when
        ResWishListDto res = wishlistService.readMyWishList(request);

        // then
        assertThat(res.getWishListId()).isEqualTo(wishlist.getId());
    }

    // READ
    @DisplayName("위시리스트 조회 실패 - 존재하지 않는 유저")
    @Test
    void readMyWishlistFailNotFoundUser() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.readMyWishList(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ
    @DisplayName("위시리스트 조회 실패 - 존재하지 않는 위시리스트")
    @Test
    void readMyWishlistFailNotFoundWishlist() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.readMyWishList(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_WISHLIST.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 항목 삭제 성공")
    @Test
    void removeItemFromWishlist() throws Exception {
        // given
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        String email = "a3acfa0a0267531ddd493ead683a99ae";
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));
        doNothing().when(wishlistItemService).deleteWishListItem(any(Wishlist.class), anyLong());

        // when
        wishlistService.removeItemFromWishList(request, itemId);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(wishlistRepository, times(1)).findByUser(user);
        verify(wishlistItemService, times(1)).deleteWishListItem(wishlist, itemId);
    }

    // DELETE
    @DisplayName("위시리스트 항목 삭제 실패 - 존재하지 않는 유저")
    @Test
    void removeItemFromWishlistFailNotFoundUser() {
        // given
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.removeItemFromWishList(request, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 항목 삭제 실패 - 존재하지 않는 위시리스트")
    @Test
    void removeItemFromWishlistFailNotFoundWishlist() {
        // given
        Long itemId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.removeItemFromWishList(request, itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_WISHLIST.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 비우기 성공")
    @Test
    void clearWishlist() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String email = "a3acfa0a0267531ddd493ead683a99ae";
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // when
        wishlistService.clearWishlist(request);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(wishlistRepository, times(1)).findByUser(user);
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 존재하지 않는 유저")
    @Test
    void clearWishlistFailNotFoundUser() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.clearWishlist(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 존재하지 않는 위시리스트")
    @Test
    void clearWishlistFailNotFoundWishlist() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> wishlistService.clearWishlist(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_WISHLIST.getMessage());
    }

    // DELETE
    @DisplayName("위시리스트 비우기 실패 - 이메일 불일치")
    @Test
    void clearWishlistFailNotMatch() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test1@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(any(User.class))).thenReturn(Optional.of(wishlist));

        // when

        // then
        assertThatThrownBy(() -> wishlistService.clearWishlist(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }
}
