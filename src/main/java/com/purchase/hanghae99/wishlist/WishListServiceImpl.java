package com.purchase.hanghae99.wishlist;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.user.UserRepository;
import com.purchase.hanghae99.wishlist.dto.ResWishListDto;
import com.purchase.hanghae99.wishlist_items.WishListItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishListServiceImpl implements WishListService {
    private final UserRepository userRepository;
    private final WishListRepository wishListRepository;
    private final WishListItemService wishListItemService;

    @Override
    @Transactional
    public Long createWishList(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        checkAccount(user.getEmail(), email);

        Wishlist wishlist = Wishlist.of(user);

        return wishListRepository.save(wishlist).getId();
    }

    @Override
    @Transactional
    public void addItemToWishList(Long wishListId, Long itemId) {
        Wishlist wishlist = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));
        wishListItemService.createWishListItem(wishlist, itemId);
    }

    @Override
    @Transactional
    public void removeItemFromWishList(Long wishListId, Long itemId) {
        Wishlist wishlist = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));
        wishListItemService.deleteWishListItem(wishlist, itemId);
    }

    @Override
    public ResWishListDto readWishList(Authentication authentication, Long wishListId) {
        checkUserExistence(authentication.getName());

        Wishlist wishlist = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_WISHLIST));

        checkAccount(wishlist.getUser().getEmail(), authentication.getName());

        return ResWishListDto.fromEntity(wishlist);
    }

    private void checkUserExistence(String emailOfConnectingUser) {
        if (userRepository.findByEmail(emailOfConnectingUser).isEmpty()) {
            throw new BusinessException(NOT_FOUND_USER);
        }
    }

    private void checkAccount(String email, String myEmail) {
        if (!email.equals(myEmail)) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }
}
