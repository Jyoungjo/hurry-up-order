package com.purchase.hanghae99.user.service;

import com.purchase.hanghae99.email.EmailService;
import com.purchase.hanghae99.user.*;
import com.purchase.hanghae99.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("이름1")
                .email("test@gmail.com")
                .password(passwordEncoder.encode("a12345678"))
                .role(UserRole.CERTIFIED_USER)
                .deletedAt(null)
                .emailVerifiedAt(null)
                .phoneNumber("010-1234-5678")
                .address("주소1")
                .build();
    }

    // CREATE
    @DisplayName("회원가입 기능 성공")
    @Test
    void signup() {
        // given
        ReqUserCreateDto req = saveUser();

        // when
        ResUserCreateDto res = userService.createUser(req);

        // then
        assertThat(res).isNotNull();
    }

    // READ ONE
    @DisplayName("회원 조회 기능 성공")
    @Test
    void readOne() {
        // given
        ReqUserCreateDto req = saveUser();
        userService.createUser(req);

        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        ResUserInfoDto res = userService.readUser(userId);

        // then
        assertThat(res.getId()).isEqualTo(userId);
    }

    // UPDATE INFO
    @DisplayName("회원 정보 수정 기능 성공")
    @Test
    void updateInfo() {
        // given
        ReqUserCreateDto req = saveUser();
        userService.createUser(req);

        ReqUserInfoUpdateDto updateReq = new ReqUserInfoUpdateDto(
                "주소2", "010-9876-5432"
        );

        Long userId = 1L;
        String originalPhoneNumber = req.getPhoneNumber();
        String originalAddress = req.getAddress();

        user.updateUserInfo(updateReq.getPhoneNumber(), updateReq.getAddress());

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        ResUserUpdateDto res = userService.updateUserInfo(userId, updateReq);

        // then
        assertThat(res.getPhoneNumber()).isNotEqualTo(originalPhoneNumber);
        assertThat(res.getAddress()).isNotEqualTo(originalAddress);
    }

    // UPDATE PASSWORD
    @DisplayName("회원 비밀번호 수정 기능 성공")
    @Test
    void updatePassword() {
        // given
        ReqUserCreateDto req = saveUser();
        userService.createUser(req);

        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345678", "a123456789!", "a123456789!"
        );

        Long userId = 1L;
        String originalPassword = req.getPassword();

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("a123456789!");
        user.updatePassword(updateReq.getNewPassword());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        userService.updateUserPassword(userId, updateReq);

        // then
        assertThat(user.getPassword()).isNotEqualTo(originalPassword);
    }

    // UPDATE EMIL VERIFICATION
    @DisplayName("회원 이메일 인증 정보 수정 기능 성공")
    @Test
    void updateEmailInfo() {
        // given
        ReqUserCreateDto req = saveUser();
        userService.createUser(req);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(emailService.checkVerificationStr(any(), any())).thenReturn(true);
        user.updateEmailVerification();

        // when
        userService.updateEmailVerification(1L, "1234");

        // then
        assertThat(user.getEmailVerifiedAt()).isNotNull();
    }

    // DELETE
    @DisplayName("회원 탈퇴 기능 성공")
    @Test
    void deleteUser() {
        // given
        ReqUserCreateDto req = saveUser();
        userService.createUser(req);

        Long userId = 1L;
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(userId, reqDto);

        // then
        assertThat(userRepository.count()).isZero();
    }

    private ReqUserCreateDto saveUser() {
        ReqUserCreateDto req = new ReqUserCreateDto(
                "test@email.com", "a123456", "이름1", "주소1", "010-1234-5678"
        );

        when(passwordEncoder.encode(any())).thenReturn("a123456");
        when(userRepository.save(any())).thenReturn(user);

        return req;
    }
}
