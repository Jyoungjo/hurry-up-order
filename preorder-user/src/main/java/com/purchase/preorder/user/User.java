package com.purchase.preorder.user;

import com.purchase.preorder.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

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
    @Column(nullable = false, unique = true)
    private String email;
    private LocalDateTime emailVerifiedAt;
    private String password;
    private String address;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private LocalDateTime deletedAt;

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
