package com.purchase.hanghae99.user;

import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.common.CustomCookieManager;
import com.purchase.hanghae99.common.RedisService;
import com.purchase.hanghae99.email.EmailDtoFactory;
import com.purchase.hanghae99.email.EmailService;
import com.purchase.hanghae99.email.ResEmailDto;
import com.purchase.hanghae99.common.security.JwtProvider;
import com.purchase.hanghae99.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99.user.dto.login.ReqLoginDto;
import com.purchase.hanghae99.user.dto.login.ResLoginDto;
import com.purchase.hanghae99.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserPwUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserUpdateDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.purchase.hanghae99.common.AesUtils.*;
import static com.purchase.hanghae99.common.CustomCookieManager.*;
import static com.purchase.hanghae99.common.exception.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final CustomCookieManager cookieManager;
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
                        throw new BusinessException(DECODING_ERROR);
                    }
                })
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
    }

    @Override
    @Transactional
    public ResUserUpdateDto updateUserInfo(Long userId, ReqUserInfoUpdateDto reqDto) throws Exception {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
        savedUser.updateUserInfo(aesCBCEncode(reqDto.getPhoneNumber()), aesCBCEncode(reqDto.getAddress()));
        return ResUserUpdateDto.fromEntity(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public ResUserPwUpdateDto updateUserPassword(Long userId, ReqUserPasswordUpdateDto reqDto) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        verifyExistingPassword(reqDto.getOriginalPassword(), savedUser.getPassword());
        verifyPassword(reqDto.getNewPassword(), reqDto.getNewPassword2());

        savedUser.updatePassword(passwordEncoder.encode(reqDto.getNewPassword()));
        return ResUserPwUpdateDto.fromEntity(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public ResEmailDto updateEmailVerification(Long userId, String userStr) throws Exception {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        if (!emailService.checkVerificationStr(aesCBCDecode(savedUser.getEmail()), userStr)) {
            return EmailDtoFactory.fail();
        }

        savedUser.updateEmailVerification();
        userRepository.save(savedUser);
        return EmailDtoFactory.succeed();
    }

    @Override
    @Transactional
    public void deleteUser(Authentication authentication, Long userId, ReqUserDeleteDto reqDto) throws Exception {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        verifyAuthentication(authentication);

        String email = authentication.getName();

        verifyAccessedUser(email, savedUser.getEmail());
        verifyPassword(reqDto.getPassword(), reqDto.getPassword2());

        userRepository.delete(savedUser);
    }

    @Override
    @Transactional
    public ResLoginDto login(HttpServletResponse response, ReqLoginDto reqDto) throws Exception {
        User user = userRepository.findByEmail(aesCBCEncode(reqDto.getEmail()))
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
            throw new BusinessException(BAD_CREDENTIALS);
        }

        if (user.getEmailVerifiedAt() == null) {
            throw new BusinessException(UNCERTIFIED_EMAIL);
        }

        String decodedEmail = aesCBCDecode(user.getEmail());

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtProvider.EMAIL_CLAIM, decodedEmail);

        String accessToken = jwtProvider.createAccessToken(claims);
        String refreshToken = jwtProvider.createRefreshToken(claims);

        cookieManager.setCookie(response, accessToken, ACCESS_TOKEN, jwtProvider.getAccessTokenExpirationPeriod());
        cookieManager.setCookie(response, refreshToken, REFRESH_TOKEN, jwtProvider.getRefreshTokenExpirationPeriod());

        redisService.setValues(decodedEmail, refreshToken, Duration.ofMillis(jwtProvider.getRefreshTokenExpirationPeriod()));

        return ResLoginDto.fromEntity(user);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieManager.getCookie(request, ACCESS_TOKEN);

        Claims claims = jwtProvider.getClaims(accessToken);
        if (claims == null) {
            throw new BusinessException(EXPIRED_JWT);
        }

        String email = claims.get("email", String.class);
        String redisRefreshToken = redisService.getValues(email);

        cookieManager.deleteCookie(response, ACCESS_TOKEN);
        cookieManager.deleteCookie(response, REFRESH_TOKEN);

        if (redisService.checkExistsValue(redisRefreshToken)) {
            redisService.deleteValuesByKey(email);

            long refreshTokenExpirationMillis = jwtProvider.getAccessTokenExpirationPeriod();
            redisService.setValues(redisRefreshToken, email, Duration.ofMillis(refreshTokenExpirationMillis));
        }
    }

    private void checkDuplicateEmail(String email) throws Exception {
        if (userRepository.existsByEmail(aesCBCEncode(email))) {
            throw new BusinessException(ALREADY_REGISTERED_EMAIL);
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber) throws Exception {
        if (userRepository.existsByPhoneNumber(aesCBCEncode(phoneNumber))) {
            throw new BusinessException(ALREADY_REGISTERED_PHONE_NUMBER);
        }
    }

    public void verifyExistingPassword(String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, newPassword)) {
            throw new BusinessException(INVALID_PASSWORD);
        }
    }

    public void verifyPassword(String password, String password2) {
        if (!password.equals(password2)) {
            throw new BusinessException(INVALID_PASSWORD);
        }
    }

    public void verifyAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }

    public void verifyAccessedUser(String authenticatedEmail, String emailOfUser) throws Exception {
        if (!authenticatedEmail.equals(aesCBCDecode(emailOfUser))) {
            throw new BusinessException(UNAUTHORIZED_ACCESS);
        }
    }
}
