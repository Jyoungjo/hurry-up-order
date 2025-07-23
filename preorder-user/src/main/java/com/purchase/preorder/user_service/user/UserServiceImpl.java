package com.purchase.preorder.user_service.user;

import com.common.core.exception.ExceptionCode;
import com.common.core.util.AesUtils;
import com.common.domain.common.UserRole;
import com.common.domain.entity.User;
import com.common.domain.repository.UserRepository;
import com.common.web.auth.AuthUtils;
import com.common.web.auth.JwtUtils;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.user_service.common.RedisService;
import com.purchase.preorder.user_service.email.EmailSender;
import com.purchase.preorder.user_service.email.ResEmailDto;
import com.purchase.preorder.user_service.user.dto.create.ReqUserCreateDto;
import com.purchase.preorder.user_service.user.dto.create.ResUserCreateDto;
import com.purchase.preorder.user_service.user.dto.delete.ReqUserDeleteDto;
import com.purchase.preorder.user_service.user.dto.login.ReqLoginDto;
import com.purchase.preorder.user_service.user.dto.login.ResLoginDto;
import com.purchase.preorder.user_service.user.dto.read.ResUserInfoDto;
import com.purchase.preorder.user_service.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.preorder.user_service.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.preorder.user_service.user.dto.update.ResUserPwUpdateDto;
import com.purchase.preorder.user_service.user.dto.update.ResUserUpdateDto;
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

import static com.common.core.exception.ExceptionCode.INVALID_EMAIL_VERIFICATION;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailSender emailSender;
    private final RedisService redisService;

    @Override
    @Transactional
    public ResUserCreateDto createUser(ReqUserCreateDto reqDto) throws Exception {
        checkDuplicateEmail(reqDto.getEmail());
        checkDuplicatePhoneNumber(reqDto.getPhoneNumber());

        // 암호화 후 Entity 생성
        String encEmail = AesUtils.aesCBCEncode(reqDto.getEmail());
        String encPassword = passwordEncoder.encode(reqDto.getPassword());
        String encName = AesUtils.aesCBCEncode(reqDto.getName());

        User user = User.of(encName, encEmail, encPassword);

        String encAddress = AesUtils.aesCBCEncode(reqDto.getAddress());
        String encPhone = AesUtils.aesCBCEncode(reqDto.getPhoneNumber());
        user.changeContactInfo(encPhone, encAddress);

        emailSender.sendMail(reqDto.getEmail());

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
        String emailOfConnectingUser = AuthUtils.getUserEmail(request, jwtUtils);

        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        verifyAccessedUser(emailOfConnectingUser, savedUser.getEmail());

        savedUser.changeContactInfo(
                AesUtils.aesCBCEncode(reqDto.getPhoneNumber()),
                AesUtils.aesCBCEncode(reqDto.getAddress())
        );

        return ResUserUpdateDto.fromEntity(savedUser);
    }


    @Override
    @Transactional
    public ResUserPwUpdateDto updateUserPassword(
            HttpServletRequest request, Long userId, ReqUserPasswordUpdateDto reqDto
    ) {
        String emailOfConnectingUser = AuthUtils.getUserEmail(request, jwtUtils);

        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        verifyAccessedUser(emailOfConnectingUser, savedUser.getEmail());

        verifyExistingPassword(reqDto.getOriginalPassword(), savedUser.getPassword());
        verifyPassword(reqDto.getNewPassword(), reqDto.getNewPassword2());

        savedUser.changePassword(passwordEncoder.encode(reqDto.getNewPassword()));

        return ResUserPwUpdateDto.fromEntity(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public void deleteUser(HttpServletRequest request, Long userId, ReqUserDeleteDto reqDto) {
        String emailOfConnectingUser = AuthUtils.getUserEmail(request, jwtUtils);

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
        String accessToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.AUTHORIZATION));

        Claims claims = jwtUtils.getClaims(accessToken);
        if (claims == null) throw new BusinessException(ExceptionCode.EXPIRED_JWT);

        deleteTokenAndAddBlacklist(claims);
    }

    // 토큰 내 claims 에 있는 이메일 기반 유저 조회
    @Override
    public User findUserByEmail(String email) {
        try {
            String encryptedEmail = AesUtils.aesCBCEncode(email);
            return userRepository.findByEmail(encryptedEmail)
                    .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));
        } catch (Exception e) {
            throw new BusinessException(ExceptionCode.ENCODING_ERROR);
        }
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.REFRESH_TOKEN_HEADER));

        if (checkRefreshToken(refreshToken)) throw new BusinessException(ExceptionCode.UNAUTHORIZED_ACCESS);

        Claims claims = jwtUtils.getClaims(refreshToken);
        String newAccessToken = jwtUtils.createAccessToken(claims);

        response.setHeader(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + newAccessToken);
    }

    @Override
    public ResEmailDto checkVerificationStr(Long userId, String verificationCode) throws Exception {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        String decodedEmail = AesUtils.aesCBCDecode(savedUser.getEmail());
        String storedString = getVerificationNumber(decodedEmail);

        if (!storedString.equals(verificationCode)) throw new BusinessException(INVALID_EMAIL_VERIFICATION);

        savedUser.verifyEmail();
        return ResEmailDto.of(decodedEmail);
    }

    private void createTokenAndSet(HttpServletResponse response, String decodedEmail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtUtils.EMAIL_CLAIM, decodedEmail);

        String accessToken = jwtUtils.createAccessToken(claims);
        String refreshToken = jwtUtils.createRefreshToken();

        response.setHeader(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + accessToken);
        response.setHeader(JwtUtils.REFRESH_TOKEN_HEADER, JwtUtils.BEARER + refreshToken);

        redisService.setValues(
                decodedEmail, refreshToken, Duration.ofMillis(jwtUtils.getRefreshTokenExpirationPeriod())
        );
    }

    private void deleteTokenAndAddBlacklist(Claims claims) {
        String email = claims.get("email", String.class);
        String redisRefreshToken = redisService.getValues(email);

        if (redisService.getValues(redisRefreshToken) != null) {
            redisService.deleteValuesByKey(email);

            long refreshTokenExpirationMillis = jwtUtils.getAccessTokenExpirationPeriod();
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

    private boolean checkRefreshToken(String refreshToken) {
        return StringUtils.hasText(refreshToken) &&
                jwtUtils.isTokenValid(refreshToken) &&
                getLogoutInfo(refreshToken);
    }

    private boolean getLogoutInfo(String refreshToken) {
        return redisService.getValues(refreshToken).equals("false");
    }

    private String getVerificationNumber(String email) {
        return redisService.getValues(email);
    }
}
