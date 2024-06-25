package com.purchase.hanghae99.user;

import com.purchase.hanghae99.common.BaseEntity;
import com.purchase.hanghae99.order.Order;
import com.purchase.hanghae99.wishlist.Wishlist;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_USER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE TB_USER SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    // TODO: 이메일 인증 객체 만들어서 수정하기
    @Column(nullable = false, unique = true)
    private String email;
    private LocalDateTime emailVerifiedAt;
    private String password;
    private String address;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime deletedAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Order> orderList;
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Wishlist> wishlists;

    public void updateUserInfo(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateEmailVerification() {
        this.role = UserRole.CERTIFIED_USER;
        this.emailVerifiedAt = LocalDateTime.now();
    }
}
