package com.purchase.preorder.wishlist_service.wishlist.service;

import com.common.core.util.JwtParser;
import com.common.domain.entity.user.Wishlist;
import com.common.domain.repository.user.WishlistRepository;
import com.common.web.auth.JwtUtils;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.user_service.user.service.UserService;
import com.purchase.preorder.wishlist_service.wishlist.dto.ResWishListDto;
import com.purchase.preorder.wishlist_service.wishlist.dto.ResWishListItemDto;
import com.purchase.preorder.wishlist_service.wishlist_item.WishlistItemService;
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
        Long userId = getUserIdFromAuthentication(request);

        Wishlist wishlist = wishListRepository.findByUserId(userId)
                .orElseGet(() -> wishListRepository.save(Wishlist.of(userId)));

        wishListItemService.createWishListItem(wishlist, itemIds);
    }

    @Override
    @Transactional
    public void removeItemFromWishList(HttpServletRequest request, List<Long> itemIds) {
        Long userId = getUserIdFromAuthentication(request);

        Wishlist wishlist = wishListRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        wishListItemService.deleteWishListItem(wishlist, itemIds);
    }

    @Override
    public ResWishListDto readMyWishList(HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(request);

        Wishlist wishlist = wishListRepository.findByUserId(userId)
                .orElseGet(() -> wishListRepository.save(Wishlist.of(userId)));

        List<ResWishListItemDto> resWishListItemDtoList = wishListItemService.fromEntities(wishlist.getWishlistItems());

        return ResWishListDto.fromEntity(wishlist, resWishListItemDtoList);
    }

    @Override
    @Transactional
    public void clearWishlist(HttpServletRequest request) {
        Long userId = getUserIdFromAuthentication(request);

        Wishlist wishlist = wishListRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        wishlist.getWishlistItems().clear();
    }

    @Override
    public void delete(Long userId) {
        wishListRepository.findByUserId(userId).ifPresent(wishListRepository::delete);
    }

    private void checkAccount(Long expected, Long actual) {
        if (!expected.equals(actual)) throw new BusinessException(UNAUTHORIZED_ACCESS);
    }

    private Long getUserIdFromAuthentication(HttpServletRequest request) {
        String accessToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.AUTHORIZATION));
        return JwtParser.getUserId(accessToken);
    }
}
