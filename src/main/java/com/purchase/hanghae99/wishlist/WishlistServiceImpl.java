package com.purchase.hanghae99.wishlist;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.user.UserRepository;
import com.purchase.hanghae99.wishlist.dto.ResWishListDto;
import com.purchase.hanghae99.wishlist_item.WishlistItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {
    private final UserRepository userRepository;
    private final WishlistRepository wishListRepository;
    private final WishlistItemService wishListItemService;

    @Override
    @Transactional
    public void addItemToWishList(Authentication authentication, Long itemId) {
        String emailOfConnectingUser = authentication.getName();

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
    public void removeItemFromWishList(Authentication authentication, Long itemId) {
        String emailOfConnectingUser = authentication.getName();

        User user = userRepository.findByEmail(emailOfConnectingUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Wishlist wishlist = wishListRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        wishListItemService.deleteWishListItem(wishlist, itemId);
    }

    @Override
    public ResWishListDto readMyWishList(Authentication authentication) {
        String emailOfConnectionUser = authentication.getName();

        User user = userRepository.findByEmail(emailOfConnectionUser)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        Wishlist wishlist = wishListRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        checkAccount(wishlist.getUser().getEmail(), authentication.getName());

        return ResWishListDto.fromEntity(wishlist);
    }

    private void checkAccount(String email, String myEmail) {
        if (!email.equals(myEmail)) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }
}
