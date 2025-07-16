package com.purchase.preorder.user_service.wishlist;

import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.user.User;
import com.purchase.preorder.user.UserRepository;
import com.purchase.preorder.user_service.wishlist.dto.ResWishListDto;
import com.purchase.preorder.user_service.wishlist.dto.ResWishListItemDto;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import com.purchase.preorder.user_service.wishlist_item.WishlistItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.purchase.preorder.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {
    private final UserRepository userRepository;
    private final WishlistRepository wishListRepository;
    private final WishlistItemService wishListItemService;

    @Override
    @Transactional
    public void addItemToWishList(HttpServletRequest request, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Wishlist wishlist = wishListRepository.findByUser(user)
                .orElseGet(() -> {
                    Wishlist newWishlist = Wishlist.of(user);
                    return wishListRepository.save(newWishlist);
                });

        wishListItemService.createWishListItem(wishlist, itemId);
    }

    @Override
    @Transactional
    public void removeItemFromWishList(HttpServletRequest request, Long itemId) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Wishlist wishlist = wishListRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        wishListItemService.deleteWishListItem(wishlist, itemId);
    }

    @Override
    public ResWishListDto readMyWishList(HttpServletRequest request) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Wishlist wishlist = wishListRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        List<ResWishListItemDto> resWishListItemDtoList = wishListItemService.fromEntities(wishlist.getWishlistItems());

        return ResWishListDto.fromEntity(wishlist, resWishListItemDtoList);
    }

    @Override
    @Transactional
    public void clearWishlist(HttpServletRequest request) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Wishlist wishlist = wishListRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        checkAccount(wishlist.getUser().getEmail(), emailOfConnectingUser);

        wishlist.getWishlistItems().clear();
        wishListRepository.save(wishlist);
    }

    private void checkAccount(String email, String myEmail) {
        if (!email.equals(myEmail)) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }

    private String getEmailOfAuthenticatedUser(HttpServletRequest request) throws Exception {
        String accessToken = CustomCookieManager.getCookie(request, CustomCookieManager.ACCESS_TOKEN);
        return AesUtils.aesCBCEncode(JwtParser.getEmail(accessToken));
    }
}
