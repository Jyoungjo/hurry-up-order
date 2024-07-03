package com.purchase.hanghae99_user.wishlist;

import com.purchase.hanghae99_user.user.User;
import com.purchase.hanghae99_user.wishlist_item.WishlistItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_WISHLIST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<WishlistItem> wishlistItems;

    public static Wishlist of(User user) {
        return Wishlist.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .wishlistItems(new ArrayList<>())
                .build();
    }
}
