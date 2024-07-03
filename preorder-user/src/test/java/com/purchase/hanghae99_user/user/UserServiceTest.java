package com.purchase.hanghae99_user.user;

import com.purchase.hanghae99_core.util.AesUtils;
import com.purchase.hanghae99_user.common.CustomCookieManager;
import com.purchase.hanghae99_user.common.RedisService;
import com.purchase.hanghae99_core.exception.BusinessException;
import com.purchase.hanghae99_user.common.security.JwtProvider;
import com.purchase.hanghae99_user.email.EmailService;
import com.purchase.hanghae99_user.email.ResEmailDto;
import com.purchase.hanghae99_user.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99_user.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99_user.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99_user.user.dto.login.ReqLoginDto;
import com.purchase.hanghae99_user.user.dto.login.ResLoginDto;
import com.purchase.hanghae99_user.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99_user.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99_user.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99_user.user.dto.update.ResUserUpdateDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.purchase.hanghae99_user.common.CustomCookieManager.ACCESS_TOKEN;
import static com.purchase.hanghae99_user.common.CustomCookieManager.REFRESH_TOKEN;
import static com.purchase.hanghae99_core.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CustomCookieManager cookieManager;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void init() throws Exception {
        user = User.builder()
                .id(1L)
                .name("fe8c75c4f0b22cfe3a5fbf1409a10b6c") // 이름1
                .email("a3acfa0a0267531ddd493ead683a99ae") // test@email.com
                .password(passwordEncoder.encode("a12345678"))
                .role(UserRole.UNCERTIFIED_USER)
                .deletedAt(null)
                .emailVerifiedAt(null)
                .phoneNumber("554b3762a1a2a3a71fc15423a0fe76a4") // 010-1234-5678
                .address("2cd158282c4d6e520a5a437e477c1b69") // 주소1
                .build();

        AesUtils aesUtils = new AesUtils();
        aesUtils.setPrivateKey("qwe123asd456zxc789q7a4z1w8s5x288");
    }

    // CREATE
    @DisplayName("회원가입 기능 성공")
    @Test
    void signup() throws Exception {
        // given
        ReqUserCreateDto req = new ReqUserCreateDto(
                "test@email.com", "a123456", "이름1", "주소1", "010-1234-5678"
        );

        // when
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        doNothing().when(emailService).sendMail(anyString());
        when(userRepository.save(any())).thenReturn(user);

        ResUserCreateDto res = userService.createUser(req);

        // then
        assertThat(res.getEmail()).isNotEmpty();
    }

    // CREATE
    @DisplayName("이미 존재하는 이메일의 경우 회원가입에 실패한다.")
    @Test
    void failSignupByDuplicatedEmail() {
        // given
        ReqUserCreateDto otherReq = new ReqUserCreateDto(
                "test@email.com", "a1234561", "이름1", "주소1", "010-1234-5672"
        );

        // when
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.createUser(otherReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ALREADY_REGISTERED_EMAIL.getMessage());
    }

    // CREATE
    @DisplayName("이미 존재하는 전화번호의 경우 회원가입에 실패한다.")
    @Test
    void failSignupByDuplicatedPhoneNumber() {
        // given
        ReqUserCreateDto otherReq = new ReqUserCreateDto(
                "test1@email.com", "a1234562", "이름1", "주소1", "010-1234-5678"
        );

        // when
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.createUser(otherReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ALREADY_REGISTERED_PHONE_NUMBER.getMessage());
    }

    // READ ONE
    @DisplayName("회원 조회 기능 성공")
    @Test
    void readOne() {
        // given
        user.updateEmailVerification();

        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        ResUserInfoDto res = userService.readUser(userId);

        // then
        assertThat(res.getId()).isEqualTo(userId);
    }

    // READ ONE
    @DisplayName("존재하지 않는 유저를 검색하면 실패한다.")
    @Test
    void failReadOneByNotFoundUser() {
        // given
        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> userService.readUser(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // READ ONE
    @DisplayName("이메일 인증을 하지 않은 유저를 검색하면 실패한다.")
    @Test
    void failReadOneByUncertifiedUser() {
        // given
        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when

        // then
        assertThatThrownBy(() -> userService.readUser(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE INFO
    @DisplayName("회원 정보 수정 기능 성공")
    @Test
    void updateInfo() throws Exception {
        // given
        ReqUserInfoUpdateDto updateReq = new ReqUserInfoUpdateDto(
                "주소2", "010-9876-5432"
        );

        Long userId = 1L;
        String originalPhoneNumber = "010-1234-5678";
        String originalAddress = "주소1";

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        user.updateUserInfo(updateReq.getPhoneNumber(), updateReq.getAddress());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ResUserUpdateDto res = userService.updateUserInfo(userId, updateReq);

        // then
        assertThat(res.getPhoneNumber()).isNotEqualTo(originalPhoneNumber);
        assertThat(res.getAddress()).isNotEqualTo(originalAddress);
    }

    // UPDATE INFO
    @DisplayName("존재하지 않는 유저의 정보를 수정하려고 할 경우 실패한다.")
    @Test
    void failUpdateInfoByNotFoundUser() {
        // given
        ReqUserInfoUpdateDto updateReq = new ReqUserInfoUpdateDto(
                "주소2", "010-9876-5432"
        );
        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> userService.updateUserInfo(userId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE PASSWORD
    @DisplayName("회원 비밀번호 수정 기능 성공")
    @Test
    void updatePassword() {
        // given
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345678", "a123456789!", "a123456789!"
        );

        Long userId = 1L;
        String originalPassword = "a123456";

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("a123456789!");
        user.updatePassword(updateReq.getNewPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        userService.updateUserPassword(userId, updateReq);

        // then
        assertThat(user.getPassword()).isNotEqualTo(originalPassword);
    }

    // UPDATE PASSWORD
    @DisplayName("존재하지 않는 회원의 비밀번호를 수정하려고 하면 실패한다.")
    @Test
    void failUpdatePasswordByNotFoundUser() {
        // given
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345678", "a123456789!", "a123456789!"
        );

        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> userService.updateUserPassword(userId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE PASSWORD
    @DisplayName("기존 비밀번호가 일치하지 않으면 실패한다.")
    @Test
    void failUpdatePasswordByNotMatchPassword() {
        // given
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345679", "a123456789!", "a123456789!"
        );

        Long userId = 1L;

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // then
        assertThatThrownBy(() -> userService.updateUserPassword(userId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(INVALID_PASSWORD.getMessage());
    }

    // UPDATE PASSWORD
    @DisplayName("새로운 비밀번호와 새로운 비밀번호를 확인하는 과정에서 일치하지 않으면 실패한다.")
    @Test
    void failUpdatePasswordByNotMatchNewPassword() {
        // given
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345678", "a123456789!", "a123456789@"
        );

        Long userId = 1L;

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.updateUserPassword(userId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(INVALID_PASSWORD.getMessage());
    }

    // UPDATE EMIAL VERIFICATION
    @DisplayName("회원 이메일 인증 정보 수정 기능 성공")
    @Test
    void updateEmailInfo() throws Exception {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(emailService.checkVerificationStr(any(), any())).thenReturn(true);
        user.updateEmailVerification();

        // when
        userService.updateEmailVerification(1L, "1234");

        // then
        assertThat(user.getEmailVerifiedAt()).isNotNull();
    }

    // UPDATE EMAIL VERIFICATION
    @DisplayName("존재하지 않는 회원의 이메일 인증 내역을 변경할 경우 실패한다.")
    @Test
    void failUpdateEmailInfoByNotFoundUser() {
        // given
        String userStr = "";

        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> userService.updateEmailVerification(userId, userStr))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE EMAIL VERIFICATION
    @DisplayName("인증코드가 일치하지 않는 경우 실패한다.")
    @Test
    void failUpdateEmailInfoByNotMatchCertificationCode() throws Exception {
        // given
        Long userId = 1L;
        String userStr = "q1a5w2s3";

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(emailService.checkVerificationStr(anyString(), anyString())).thenReturn(false);

        // when
        ResEmailDto failDto = userService.updateEmailVerification(userId, userStr);

        // then
        assertThat(failDto.getStatus()).isEqualTo(false);
    }

    // DELETE
    @DisplayName("회원 탈퇴 기능 성공")
    @Test
    void deleteUser() throws Exception {
        // given
        Long userId = 1L;
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );
        Authentication authentication = new TestingAuthenticationToken("test@email.com", null, String.valueOf(UserRole.CERTIFIED_USER));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(authentication, userId, reqDto);

        // then
        assertThat(userRepository.count()).isZero();
    }

    // DELETE
    @DisplayName("존재하지 않는 회원의 탈퇴를 진행할 경우 실패한다.")
    @Test
    void failDeleteUserByNotFoundUser() {
        // given
        Long userId = 1L;
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );
        Authentication authentication = new TestingAuthenticationToken("test@email.com", null, String.valueOf(UserRole.CERTIFIED_USER));

        // when
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.deleteUser(authentication, userId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("인증 정보가 존재하지 않을 경우 실패한다.")
    @Test
    void failDeleteUserByNotExistedAuthentication() {
        // given
        Long userId = 1L;
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> userService.deleteUser(null, userId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // DELETE
    @DisplayName("접속한 유저와 uri가 일치하지 않을 경우 실패한다.")
    @Test
    void failDeleteUserByNotAccessedUser() throws Exception {
        // given
        Long userId = 1L;
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );

        Authentication authentication = new TestingAuthenticationToken("test2@email.com", null, String.valueOf(UserRole.CERTIFIED_USER));

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> userService.deleteUser(authentication, userId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED_ACCESS.getMessage());
    }

    // LOGIN
    @DisplayName("로그인 기능 성공")
    @Test
    void login() throws Exception {
        // given
        ReqLoginDto reqDto = new ReqLoginDto("test@email.com", "a12345678!");

        user.updateEmailVerification();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        String email = user.getEmail();

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtProvider.EMAIL_CLAIM, email);
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";
        String refreshToken = "evfjklwhalrhjlkhnajklwrhfjkldhjakwlfhejkawlhfjklewajhklefhjklads";

        when(jwtProvider.createAccessToken(any())).thenReturn(accessToken);
        when(jwtProvider.createRefreshToken(any())).thenReturn(refreshToken);

        when(jwtProvider.getAccessTokenExpirationPeriod()).thenReturn(900000L);
        when(jwtProvider.getRefreshTokenExpirationPeriod()).thenReturn(86400000L);

        MockHttpServletResponse response = new MockHttpServletResponse();
        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(0);
            String token = invocation.getArgument(1);
            String name = invocation.getArgument(2);
            long expiry = invocation.getArgument(3);

            ResponseCookie responseCookie = ResponseCookie.from(name, token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(expiry)
                    .build();
            resp.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            return null;
        }).when(cookieManager).setCookie(any(HttpServletResponse.class), anyString(), anyString(), anyLong());

        doNothing().when(redisService).setValues(any(), any(), any(Duration.class));

        // when
        ResLoginDto resDto = userService.login(response, reqDto);

        // then
        assertThat(resDto.getEmail()).isEqualTo(email);
        assertThat(response.getCookies()).isNotEmpty();
        assertThat(Arrays.stream(response.getCookies()).anyMatch(cookie -> ACCESS_TOKEN.equals(cookie.getName()))).isTrue();
        assertThat(Arrays.stream(response.getCookies()).anyMatch(cookie -> REFRESH_TOKEN.equals(cookie.getName()))).isTrue();
    }

    // LOGIN
    @DisplayName("등록된 이메일이 아니라면 로그인이 실패한다.")
    @Test
    void failLoginByNotExist() {
        // given
        ReqLoginDto reqDto = new ReqLoginDto("test@email.com", "a12345678!");

        user.updateEmailVerification();

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.login(response, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // LOGIN
    @DisplayName("비밀번호가 일치하지 않으면 실패한다.")
    @Test
    void failLoginByNotMatchPassword() {
        // given
        ReqLoginDto reqDto = new ReqLoginDto("test@email.com", "a12345679!");

        user.updateEmailVerification();

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // then
        assertThatThrownBy(() -> userService.login(response, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(BAD_CREDENTIALS.getMessage());
    }

    // LOGIN
    @DisplayName("이메일 인증을 하지 않을 경우 로그인에 실패한다.")
    @Test
    void failLoginByEmailVerification() {
        // given
        ReqLoginDto reqDto = new ReqLoginDto("test@email.com", "a12345678!");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.login(response, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNCERTIFIED_EMAIL.getMessage());
    }

    // LOGOUT
    @DisplayName("로그아웃 기능 성공")
    @Test
    void logout() {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";
        String refreshToken = "evfjklwhalrhjlkhnajklwrhfjkldhjakwlfhejkawlhfjklewajhklefhjklads";
        when(cookieManager.getCookie(any(), anyString())).thenReturn(accessToken);

        Claims claims = Jwts.claims();
        claims.put("email", "test@email.com");
        when(jwtProvider.getClaims(anyString())).thenReturn(claims);

        when(redisService.getValues(anyString())).thenReturn(refreshToken);

        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(0);
            String name = invocation.getArgument(1);

            ResponseCookie cookie = ResponseCookie.from(name, "")
                    .maxAge(0)
                    .path("/")
                    .httpOnly(true)
                    .build();
            return null;
        }).when(cookieManager).deleteCookie(any(HttpServletResponse.class), anyString());

        when(redisService.checkExistsValue(anyString())).thenReturn(true);
        doNothing().when(redisService).deleteValuesByKey(anyString());
        when(jwtProvider.getAccessTokenExpirationPeriod()).thenReturn(900000L);
        doNothing().when(redisService).setValues(anyString(), anyString(), any(Duration.class));

        // when
        userService.logout(req, res);

        // then
        assertThat(res.getCookies()).isEmpty();
        assertThat(Arrays.stream(res.getCookies()).anyMatch(cookie -> ACCESS_TOKEN.equals(cookie.getName()))).isFalse();
        assertThat(Arrays.stream(res.getCookies()).anyMatch(cookie -> REFRESH_TOKEN.equals(cookie.getName()))).isFalse();
    }

    // LOGOUT
    @DisplayName("Claims가 없다면 로그아웃에 실패한다.")
    @Test
    void failLogoutByNotFoundClaims() {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        // when
        when(cookieManager.getCookie(any(), anyString())).thenReturn(accessToken);
        when(jwtProvider.getClaims(anyString())).thenReturn(null);

        // then
        assertThatThrownBy(() -> userService.logout(req, res))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(EXPIRED_JWT.getMessage());
    }
}
