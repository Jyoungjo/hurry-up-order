package com.common.domain.repository.user;

import com.common.domain.entity.user.User;
import com.common.domain.entity.user.projection.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByIdAndEmail(Long id, String email);
    boolean existsByIdAndPassword(Long id, String password);
    Optional<User> findByEmail(String email);
    Optional<LoginInfo> findLoginInfoByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.phone = :phone, u.address = :addr WHERE u.id = :id")
    void updateContactInfo(@Param("id") Long id, @Param("phone") String phone, @Param("addr")  String addr);

    @Modifying
    @Query("UPDATE User u SET u.password = :pw WHERE u.id = :id")
    void updatePassword(@Param("id") Long id, @Param("pw") String password);
}
