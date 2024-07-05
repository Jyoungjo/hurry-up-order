package com.purchase.preorder.user;

import com.purchase.preorder.common.JwtUtils;
import com.purchase.preorder.common.RedisService;
import com.purchase.preorder.email.EmailDtoFactory;
import com.purchase.preorder.email.EmailService;
import com.purchase.preorder.email.ResEmailDto;
import com.purchase.preorder.exception.BusinessException;
import com.purchase.preorder.exception.ExceptionCode;
import com.purchase.preorder.user.dto.create.ReqUserCreateDto;
import com.purchase.preorder.user.dto.create.ResUserCreateDto;
import com.purchase.preorder.user.dto.delete.ReqUserDeleteDto;
import com.purchase.preorder.user.dto.login.ReqLoginDto;
import com.purchase.preorder.user.dto.login.ResLoginDto;
import com.purchase.preorder.user.dto.read.ResUserInfoDto;
import com.purchase.preorder.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.preorder.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.preorder.user.dto.update.ResUserPwUpdateDto;
import com.purchase.preorder.user.dto.update.ResUserUpdateDto;
import com.purchase.preorder.util.AesUtils;
import com.purchase.preorder.util.CustomCookieManager;
import com.purchase.preorder.util.JwtParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtils jwtProvider;
    private final RedisService redisService;

    @Override
    @Transactional
    public ResUserCreateDto createUser(ReqUserCreateDto reqDto) throws Exception {
        checkDuplicateEmail(reqDto.getEmail());
        checkDuplicatePhoneNumber(reqDto.getPhoneNumber());

        User user = reqDto.toEntity(passwordEncoder);

        emailService.sendMail(reqDto.getEmail());

        return ResUserCreateDto.fromEntity(userRepository.save(user));
    }

    @Override
    public ResUserInfoDto readUser(Long userId) {
        return userRepository.findById(userId)
                .filter(x -> x.getRole().equals(UserRole.CERTIFIED_USER))
                .map(user -> {
                    try {
                        return ResUserInfoDto.fromEntity(user);
                    } catch (Exception e) {
                        throw new BusinessException(ExceptionCode.DECODING_ERROR);
                    }
                })
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));
    }

    @Override
    @Transactional
    public ResUserUpdateDto updateUserInfo(
            HttpServletRequest request, Long userId, ReqUserInfoUpdateDto reqDto
    ) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        verifyAccessedUser(emailOfConnectingUser, savedUser.getEmail());

        savedUser.updateUserInfo(
                AesUtils.aesCBCEncode(reqDto.getPhoneNumber()), AesUtils.aesCBCEncode(reqDto.getAddress())
        );
        return ResUserUpdateDto.fromEntity(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public ResUserPwUpdateDto updateUserPassword(
            HttpServletRequest request, Long userId, ReqUserPasswordUpdateDto reqDto
    ) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        verifyAccessedUser(emailOfConnectingUser, savedUser.getEmail());

        verifyExistingPassword(reqDto.getOriginalPassword(), savedUser.getPassword());
        verifyPassword(reqDto.getNewPassword(), reqDto.getNewPassword2());

        savedUser.updatePassword(passwordEncoder.encode(reqDto.getNewPassword()));
        return ResUserPwUpdateDto.fromEntity(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public ResEmailDto updateEmailVerification(Long userId, String userStr) throws Exception {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        if (!emailService.checkVerificationStr(AesUtils.aesCBCDecode(savedUser.getEmail()), userStr)) {
            return EmailDtoFactory.fail();
        }

        savedUser.updateEmailVerification();
        userRepository.save(savedUser);
        return EmailDtoFactory.succeed();
    }

    @Override
    @Transactional
    public void deleteUser(HttpServletRequest request, Long userId, ReqUserDeleteDto reqDto) throws Exception {
        String emailOfConnectingUser = getEmailOfAuthenticatedUser(request);

        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        verifyAccessedUser(emailOfConnectingUser, savedUser.getEmail());
        verifyPassword(reqDto.getPassword(), reqDto.getPassword2());

        userRepository.delete(savedUser);
    }

    @Override
    @Transactional
    public ResLoginDto login(HttpServletResponse response, ReqLoginDto reqDto) throws Exception {
        User user = userRepository.findByEmail(AesUtils.aesCBCEncode(reqDto.getEmail()))
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ExceptionCode.BAD_CREDENTIALS);
        }

        if (user.getEmailVerifiedAt() == null) {
            throw new BusinessException(ExceptionCode.UNCERTIFIED_EMAIL);
        }

        String decodedEmail = AesUtils.aesCBCDecode(user.getEmail());

        createTokenAndSet(response, decodedEmail);

        return ResLoginDto.fromEntity(user);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = CustomCookieManager.getCookie(request, CustomCookieManager.ACCESS_TOKEN);

        Claims claims = jwtProvider.getClaims(accessToken);
        if (claims == null) {
            throw new BusinessException(ExceptionCode.EXPIRED_JWT);
        }

        deleteTokenAndAddBlacklist(response, claims);
    }

    @Override
    @Transactional
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CustomCookieManager.getCookie(request, CustomCookieManager.REFRESH_TOKEN);

        if (checkRefreshToken(refreshToken)) {
            throw new BusinessException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }

        Claims claims = jwtProvider.getClaims(refreshToken);
        String newAccessToken = jwtProvider.createAccessToken(claims);

        CustomCookieManager.setCookie(
                response, newAccessToken,
                CustomCookieManager.ACCESS_TOKEN, jwtProvider.getAccessTokenExpirationPeriod()
        );
    }

    private void createTokenAndSet(HttpServletResponse response, String decodedEmail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtUtils.EMAIL_CLAIM, decodedEmail);

        String accessToken = jwtProvider.createAccessToken(claims);
        String refreshToken = jwtProvider.createRefreshToken(claims);

        CustomCookieManager.setCookie(
                response, accessToken, CustomCookieManager.ACCESS_TOKEN, jwtProvider.getAccessTokenExpirationPeriod()
        );
        CustomCookieManager.setCookie(
                response, refreshToken, CustomCookieManager.REFRESH_TOKEN, jwtProvider.getRefreshTokenExpirationPeriod()
        );

        redisService.setValues(
                decodedEmail, refreshToken, Duration.ofMillis(jwtProvider.getRefreshTokenExpirationPeriod())
        );
    }

    private void deleteTokenAndAddBlacklist(HttpServletResponse response, Claims claims) {
        String email = claims.get("email", String.class);
        String redisRefreshToken = redisService.getValues(email);

        CustomCookieManager.deleteCookie(response, CustomCookieManager.ACCESS_TOKEN);
        CustomCookieManager.deleteCookie(response, CustomCookieManager.REFRESH_TOKEN);

        if (redisService.checkExistsValue(redisRefreshToken)) {
            redisService.deleteValuesByKey(email);

            long refreshTokenExpirationMillis = jwtProvider.getAccessTokenExpirationPeriod();
            redisService.setValues(redisRefreshToken, email, Duration.ofMillis(refreshTokenExpirationMillis));
        }
    }

    private void checkDuplicateEmail(String email) throws Exception {
        if (userRepository.existsByEmail(AesUtils.aesCBCEncode(email))) {
            throw new BusinessException(ExceptionCode.ALREADY_REGISTERED_EMAIL);
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber) throws Exception {
        if (userRepository.existsByPhoneNumber(AesUtils.aesCBCEncode(phoneNumber))) {
            throw new BusinessException(ExceptionCode.ALREADY_REGISTERED_PHONE_NUMBER);
        }
    }

    public void verifyExistingPassword(String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, newPassword)) {
            throw new BusinessException(ExceptionCode.INVALID_PASSWORD);
        }
    }

    public void verifyPassword(String password, String password2) {
        if (!password.equals(password2)) {
            throw new BusinessException(ExceptionCode.INVALID_PASSWORD);
        }
    }

    public void verifyAccessedUser(String authenticatedEmail, String emailOfUser) {
        if (!emailOfUser.equals(authenticatedEmail)) {
            throw new BusinessException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }
    }

    private String getEmailOfAuthenticatedUser(HttpServletRequest request) throws Exception {
        String accessToken = CustomCookieManager.getCookie(request, CustomCookieManager.ACCESS_TOKEN);
        return AesUtils.aesCBCEncode(JwtParser.getEmail(accessToken));
    }

    private boolean checkRefreshToken(String refreshToken) {
        return StringUtils.hasText(refreshToken) &&
                jwtProvider.isTokenValid(refreshToken) &&
                getLogoutInfo(refreshToken);
    }

    private boolean getLogoutInfo(String refreshToken) {
        return redisService.getValues(refreshToken).equals("false");
    }
}
