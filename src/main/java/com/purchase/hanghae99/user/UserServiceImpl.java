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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
    public ResUserCreateDto createUser(ReqUserCreateDto reqDto) {
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
                .map(ResUserInfoDto::fromEntity)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
    }

    @Override
    @Transactional
    public ResUserUpdateDto updateUserInfo(Long userId, ReqUserInfoUpdateDto reqDto) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
        savedUser.updateUserInfo(reqDto.getPhoneNumber(), reqDto.getAddress());
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
    public ResEmailDto updateEmailVerification(Long userId, String userStr) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        if (!emailService.checkVerificationStr(savedUser.getEmail(), userStr)) {
            return EmailDtoFactory.fail();
        }

        savedUser.updateEmailVerification();
        userRepository.save(savedUser);
        return EmailDtoFactory.succeed();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, ReqUserDeleteDto reqDto) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        verifyPassword(reqDto.getPassword(), reqDto.getPassword2());

        userRepository.delete(savedUser);
    }

    @Override
    @Transactional
    public ResLoginDto login(HttpServletResponse response, ReqLoginDto reqDto) {
        User user = userRepository.findByEmail(reqDto.getEmail())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
            throw new BusinessException(BAD_CREDENTIALS);
        }

        if (user.getEmailVerifiedAt() == null) {
            throw new BusinessException(UNCERTIFIED_EMAIL);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtProvider.EMAIL_CLAIM, user.getEmail());

        String accessToken = jwtProvider.createAccessToken(claims);
        String refreshToken = jwtProvider.createRefreshToken();

        cookieManager.setCookie(response, accessToken, ACCESS_TOKEN, jwtProvider.getAccessTokenExpirationPeriod());
        cookieManager.setCookie(response, refreshToken, REFRESH_TOKEN, jwtProvider.getRefreshTokenExpirationPeriod());

        redisService.setValues(user.getEmail(), refreshToken, Duration.ofMillis(jwtProvider.getRefreshTokenExpirationPeriod()));

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

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ALREADY_REGISTERED_EMAIL);
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
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
}
