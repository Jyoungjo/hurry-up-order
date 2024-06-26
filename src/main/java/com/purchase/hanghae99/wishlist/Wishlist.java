package com.purchase.hanghae99.wishlist;

import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.wishlist_item.WishlistItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "wish_list", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<WishlistItem> wishlistItems;

    public static Wishlist of(User user) {
        return Wishlist.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
