package com.purchase.preorder.user;

import com.purchase.preorder.user_service.common.RedisService;
import com.purchase.preorder.user_service.email.EmailService;
import com.purchase.preorder.user_service.email.ResEmailDto;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.user_service.user.UserServiceImpl;
import com.purchase.preorder.user_service.user.dto.create.ReqUserCreateDto;
import com.purchase.preorder.user_service.user.dto.create.ResUserCreateDto;
import com.purchase.preorder.user_service.user.dto.delete.ReqUserDeleteDto;
import com.purchase.preorder.user_service.user.dto.login.ReqLoginDto;
import com.purchase.preorder.user_service.user.dto.login.ResLoginDto;
import com.purchase.preorder.user_service.user.dto.read.ResUserInfoDto;
import com.purchase.preorder.user_service.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.preorder.user_service.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.preorder.user_service.user.dto.update.ResUserUpdateDto;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.purchase.preorder.exception.ExceptionCode.*;
import static com.purchase.preorder.util.CustomCookieManager.ACCESS_TOKEN;
import static com.purchase.preorder.util.CustomCookieManager.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtils jwtProvider;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private static MockedStatic<JwtParser> jwtParser;
    private static MockedStatic<CustomCookieManager> cookieManager;

    @BeforeEach
    void init() {
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

        JwtParser jwtParser = new JwtParser();
        jwtParser.setKey("abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789");
    }

    @BeforeAll
    static void beforeAll() {
        jwtParser = mockStatic(JwtParser.class);
        cookieManager = mockStatic(CustomCookieManager.class);
    }

    @AfterAll
    static void afterAll() {
        jwtParser.close();
        cookieManager.close();
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
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserInfoUpdateDto updateReq = new ReqUserInfoUpdateDto(
                "주소2", "010-9876-5432"
        );

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        // a3acfa0a0267531ddd493ead683a99ae
        Long userId = 1L;
        String originalPhoneNumber = "010-1234-5678";
        String originalAddress = "주소1";

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        user.updateUserInfo(updateReq.getPhoneNumber(), updateReq.getAddress());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ResUserUpdateDto res = userService.updateUserInfo(request, userId, updateReq);

        // then
        assertThat(res.getPhoneNumber()).isNotEqualTo(originalPhoneNumber);
        assertThat(res.getAddress()).isNotEqualTo(originalAddress);
    }

    // UPDATE INFO
    @DisplayName("존재하지 않는 유저의 정보를 수정하려고 할 경우 실패한다.")
    @Test
    void failUpdateInfoByNotFoundUser() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserInfoUpdateDto updateReq = new ReqUserInfoUpdateDto(
                "주소2", "010-9876-5432"
        );

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> userService.updateUserInfo(request, userId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE PASSWORD
    @DisplayName("회원 비밀번호 수정 기능 성공")
    @Test
    void updatePassword() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345678", "a123456789!", "a123456789!"
        );

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        Long userId = 1L;
        String originalPassword = "a123456";

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("a123456789!");
        user.updatePassword(updateReq.getNewPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        userService.updateUserPassword(request, userId, updateReq);

        // then
        assertThat(user.getPassword()).isNotEqualTo(originalPassword);
    }

    // UPDATE PASSWORD
    @DisplayName("존재하지 않는 회원의 비밀번호를 수정하려고 하면 실패한다.")
    @Test
    void failUpdatePasswordByNotFoundUser() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345678", "a123456789!", "a123456789!"
        );

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> userService.updateUserPassword(request, userId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // UPDATE PASSWORD
    @DisplayName("기존 비밀번호가 일치하지 않으면 실패한다.")
    @Test
    void failUpdatePasswordByNotMatchPassword() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345679", "a123456789!", "a123456789!"
        );

        Long userId = 1L;

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // then
        assertThatThrownBy(() -> userService.updateUserPassword(request, userId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(INVALID_PASSWORD.getMessage());
    }

    // UPDATE PASSWORD
    @DisplayName("새로운 비밀번호와 새로운 비밀번호를 확인하는 과정에서 일치하지 않으면 실패한다.")
    @Test
    void failUpdatePasswordByNotMatchNewPassword() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserPasswordUpdateDto updateReq = new ReqUserPasswordUpdateDto(
                "a12345678", "a123456789!", "a123456789@"
        );

        Long userId = 1L;

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.updateUserPassword(request, userId, updateReq))
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
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(request, userId, reqDto);

        // then
        assertThat(userRepository.count()).isZero();
    }

    // DELETE
    @DisplayName("존재하지 않는 회원의 탈퇴를 진행할 경우 실패한다.")
    @Test
    void failDeleteUserByNotFoundUser() {
        // given
        Long userId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        // when
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.deleteUser(request, userId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    // DELETE
    @DisplayName("접속한 유저와 이메일이 일치하지 않을 경우 실패한다.")
    @Test
    void failDeleteUserByNotAccessedUser() {
        // given
        Long userId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        ReqUserDeleteDto reqDto = new ReqUserDeleteDto(
                "a12345678", "a12345678"
        );

        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";

        request.setCookies(new MockCookie("accessToken", accessToken));

        jwtParser.when(() -> JwtParser.getEmail(anyString())).thenReturn("test1@email.com");
        cookieManager.when(() -> CustomCookieManager.getCookie(any(HttpServletRequest.class), anyString()))
                .thenReturn(accessToken);

        // when
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> userService.deleteUser(request, userId, reqDto))
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
        claims.put(JwtUtils.EMAIL_CLAIM, email);
        String accessToken = "hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl";
        String refreshToken = "evfjklwhalrhjlkhnajklwrhfjkldhjakwlfhejkawlhfjklewajhklefhjklads";

        when(jwtProvider.createAccessToken(any())).thenReturn(accessToken);
        when(jwtProvider.createRefreshToken(any())).thenReturn(refreshToken);

        when(jwtProvider.getAccessTokenExpirationPeriod()).thenReturn(900000L);
        when(jwtProvider.getRefreshTokenExpirationPeriod()).thenReturn(86400000L);

        MockHttpServletResponse response = new MockHttpServletResponse();
        cookieManager.when(() -> CustomCookieManager.setCookie(
                any(HttpServletResponse.class), anyString(), anyString(), anyLong())).thenAnswer(invocation -> {
            HttpServletResponse res = invocation.getArgument(0);
            String value = invocation.getArgument(1);
            String name = invocation.getArgument(2);
            long maxAge = invocation.getArgument(3);

            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge((int) maxAge);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return null;
        });

        doNothing().when(redisService).setValues(any(), any(), any(Duration.class));

        // when
        ResLoginDto resDto = userService.login(response, reqDto);

        // then
        assertThat(resDto.getEmail()).isEqualTo(email);
        assertThat(response.getCookies()).isNotEmpty();
        assertThat(Arrays.stream(response.getCookies())
                .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN))
                .findFirst()
                .get()
                .getValue()).isEqualTo("hdjkslafhjkljeklhajkwlhfjkldhsajklfhujelwhmrklejkl21h3jlk1h24jkl");
        assertThat(Arrays.stream(response.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN))
                .findFirst()
                .get()
                .getValue()).isEqualTo("evfjklwhalrhjlkhnajklwrhfjkldhjakwlfhejkawlhfjklewajhklefhjklads");
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

        cookieManager.when(() -> CustomCookieManager.getCookie(req, ACCESS_TOKEN)).thenReturn(accessToken);
        cookieManager.when(() -> CustomCookieManager.getCookie(req, REFRESH_TOKEN)).thenReturn(refreshToken);

        Claims claims = Jwts.claims();
        claims.put("email", "test@email.com");
        when(jwtProvider.getClaims(anyString())).thenReturn(claims);

        when(redisService.getValues(anyString())).thenReturn(refreshToken);

        when(redisService.checkExistsValue(anyString())).thenReturn(true);
        doNothing().when(redisService).deleteValuesByKey(anyString());
        when(jwtProvider.getAccessTokenExpirationPeriod()).thenReturn(900000L);
        doNothing().when(redisService).setValues(anyString(), anyString(), any(Duration.class));

        // when
        userService.logout(req, res);

        // then
        assertThat(Arrays.stream(res.getCookies())
                .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN))
                .findFirst()).isEmpty();
        assertThat(Arrays.stream(res.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN))
                .findFirst()).isEmpty();
    }

    // LOGOUT
    @DisplayName("Claims가 없다면 로그아웃에 실패한다.")
    @Test
    void failLogoutByNotFoundClaims() {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        // when
        cookieManager.when(() -> CustomCookieManager.getCookie(req, ACCESS_TOKEN)).thenReturn("");
        when(jwtProvider.getClaims(anyString())).thenReturn(null);

        // then
        assertThatThrownBy(() -> userService.logout(req, res))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(EXPIRED_JWT.getMessage());
    }
}
