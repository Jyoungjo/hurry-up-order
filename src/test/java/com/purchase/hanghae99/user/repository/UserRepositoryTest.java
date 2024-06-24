package com.purchase.hanghae99.user.repository;

import com.purchase.hanghae99.config.JpaConfig;
import com.purchase.hanghae99.config.PasswordEncoderTestConfig;
import com.purchase.hanghae99.user.User;
import com.purchase.hanghae99.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.purchase.hanghae99.user.UserRole.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaConfig.class, PasswordEncoderTestConfig.class})
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user() {
        return User.builder()
                .id(1L)
                .email("email1@gmail.com")
                .role(UNCERTIFIED_USER)
                .name("이름1")
                .phoneNumber("010-1234-5678")
                .address("주소1")
                .deletedAt(null)
                .emailVerifiedAt(null)
                .password(passwordEncoder.encode("asd1234!!"))
                .build();
    }

    // CREATE
    @DisplayName("유저 회원가입 성공")
    @Test
    void successRegister() {
        // given
        User user = user();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(user.getEmail()).isEqualTo(savedUser.getEmail());
    }

    // READ ONE
    @DisplayName("유저 정보 조회")
    @Test
    void readUser() {
        // given
        User savedUser = userRepository.save(user());

        // when
        // TODO: 이메일 인증 후에 유저 정보 조회하는 로직 테스트해야함
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(savedUser.getName());
    }

    // UPDATE INFO
    @DisplayName("유저 정보 수정(비밀번호 제외)")
    @Test
    void updateInfo() {
        // given
        User savedUser = userRepository.save(user());

        // when
        String originalAddress = savedUser.getAddress();
        String originalPhoneNumber = savedUser.getPhoneNumber();

        savedUser.updateUserInfo("010-1234-1234", "주소2");
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getAddress()).isNotEqualTo(originalAddress);
        assertThat(updatedUser.getPhoneNumber()).isNotEqualTo(originalPhoneNumber);
    }

    // UPDATE PASSWORD
    @DisplayName("유저 비밀번호 수정")
    @Test
    void updatePassword() {
        // given
        User savedUser = userRepository.save(user());

        // when
        String originalPassword = savedUser.getPassword();

        savedUser.updatePassword(passwordEncoder.encode("asd1235!!"));
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getPassword()).isNotEqualTo(originalPassword);
    }

    // UPDATE EMAIL VERIFICATION
    @DisplayName("유저 이메일 인증 정보 수정")
    @Test
    void updateEmailInfo() {
        // given
        User savedUser = userRepository.save(user());

        // when
        savedUser.updateEmailVerification();
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getEmailVerifiedAt()).isNotNull();
    }

    // DELETE
    @DisplayName("유저 회원 탈퇴")
    @Test
    void deleteUser() {
        // given
        User savedUser = userRepository.save(user());

        // when
        userRepository.delete(savedUser);

        // then
        assertThat(userRepository.count()).isZero();
    }
}