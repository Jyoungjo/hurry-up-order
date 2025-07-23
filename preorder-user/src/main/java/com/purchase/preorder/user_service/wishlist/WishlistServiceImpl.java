package com.purchase.preorder.user_service.wishlist;

import com.common.core.util.JwtParser;
import com.common.domain.entity.User;
import com.common.domain.entity.Wishlist;
import com.common.domain.repository.WishlistRepository;
import com.common.web.auth.JwtUtils;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.user_service.user.UserService;
import com.purchase.preorder.user_service.wishlist.dto.ResWishListDto;
import com.purchase.preorder.user_service.wishlist.dto.ResWishListItemDto;
import com.purchase.preorder.user_service.wishlist_item.WishlistItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.common.core.exception.ExceptionCode.NOT_FOUND_WISHLIST;
import static com.common.core.exception.ExceptionCode.UNAUTHORIZED_ACCESS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {

    private final UserService userService;
    private final WishlistRepository wishListRepository;
    private final WishlistItemService wishListItemService;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void addItemToWishList(HttpServletRequest request, List<Long> itemIds) {
        User user = getUserByRequest(request);

        Wishlist wishlist = wishListRepository.findByUserId(user.getId())
                .orElseGet(() -> wishListRepository.save(Wishlist.of(user.getId())));

        wishListItemService.createWishListItem(wishlist, itemIds);
    }

    @Override
    @Transactional
    public void removeItemFromWishList(HttpServletRequest request, List<Long> itemIds) {
        Wishlist wishlist = getWishlistByRequest(request);

        wishListItemService.deleteWishListItem(wishlist, itemIds);
    }

    @Override
    public ResWishListDto readMyWishList(HttpServletRequest request) {
        Wishlist wishlist = getWishlistByRequest(request);

        List<ResWishListItemDto> resWishListItemDtoList = wishListItemService.fromEntities(wishlist.getWishlistItems());

        return ResWishListDto.fromEntity(wishlist, resWishListItemDtoList);
    }

    @Override
    @Transactional
    public void clearWishlist(HttpServletRequest request) {
        User user = getUserByRequest(request);

        Wishlist wishlist = wishListRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        checkAccount(wishlist.getUserId(), user.getId());

        wishlist.getWishlistItems().clear();
    }

    private Wishlist getWishlistByRequest(HttpServletRequest request) {
        User user = getUserByRequest(request);

        return wishListRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));
    }

    private User getUserByRequest(HttpServletRequest request) {
        String email = getEmailOfAuthenticatedUser(request);
        return userService.findUserByEmail(email);
    }

    private void checkAccount(Long expected, Long actual) {
        if (!expected.equals(actual)) throw new BusinessException(UNAUTHORIZED_ACCESS);
    }

    private String getEmailOfAuthenticatedUser(HttpServletRequest request) {
        String accessToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.AUTHORIZATION));
        return JwtParser.getEmail(accessToken);
    }
}
