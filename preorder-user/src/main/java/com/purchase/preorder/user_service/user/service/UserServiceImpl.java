package com.purchase.preorder.user_service.user.service;

import com.common.core.exception.ExceptionCode;
import com.common.core.util.AesUtils;
import com.common.domain.common.UserRole;
import com.common.domain.entity.user.User;
import com.common.domain.entity.user.projection.LoginInfo;
import com.common.domain.repository.user.UserRepository;
import com.common.event_common.domain_event_vo.user.UserCreatedDomainEvent;
import com.common.event_common.domain_event_vo.user.UserDeletedDomainEvent;
import com.common.event_common.mapper.UserDomainEventMapper;
import com.common.event_common.publisher.DomainEventPublisher;
import com.common.web.auth.AuthUtils;
import com.common.web.auth.JwtUtils;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.email_service.email.ResEmailDto;
import com.purchase.preorder.user_service.user.dto.create.ReqUserCreateDto;
import com.purchase.preorder.user_service.user.dto.create.ResUserCreateDto;
import com.purchase.preorder.user_service.user.dto.delete.ReqUserDeleteDto;
import com.purchase.preorder.user_service.user.dto.login.ReqLoginDto;
import com.purchase.preorder.user_service.user.dto.login.ResLoginDto;
import com.purchase.preorder.user_service.user.dto.read.ResUserInfoDto;
import com.purchase.preorder.user_service.user.dto.update.*;
import com.purchase.preorder.user_service_common.util.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.common.core.exception.ExceptionCode.INVALID_EMAIL_VERIFICATION;
import static com.common.core.exception.ExceptionCode.UNAUTHORIZED_ACCESS;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisService redisService;
    private final DomainEventPublisher publisher;
    private final UserDomainEventMapper mapper;
    private final UserCacheHelper cacheHelper;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils,
                           RedisService redisService,
                           @Qualifier("userDomainEventPublisher") DomainEventPublisher publisher,
                           UserDomainEventMapper mapper,
                           UserCacheHelper cacheHelper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.redisService = redisService;
        this.publisher = publisher;
        this.mapper = mapper;
        this.cacheHelper = cacheHelper;
    }

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

        UserCreatedDomainEvent event = mapper.toUserCreatedEvent(user);
        publisher.publishOnlySpringEventAfterCommit(event);

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

        verifyAccessedUser(userId, emailOfConnectingUser);

        userRepository.updateContactInfo(
                userId,
                AesUtils.aesCBCEncode(reqDto.getPhoneNumber()),
                AesUtils.aesCBCEncode(reqDto.getAddress())
        );

        return ResUserUpdateDto.of(userId, reqDto.getAddress(), reqDto.getPhoneNumber());
    }


    @Override
    @Transactional
    public ResUserPwUpdateDto updateUserPassword(
            HttpServletRequest request, Long userId, ReqUserPasswordUpdateDto reqDto
    ) {
        String emailOfConnectingUser = AuthUtils.getUserEmail(request, jwtUtils);

        verifyAccessedUser(userId, emailOfConnectingUser);
        verifyExistingPassword(userId, reqDto.getOriginalPassword());
        verifyPassword(reqDto.getNewPassword(), reqDto.getNewPassword2());

        userRepository.updatePassword(userId, passwordEncoder.encode(reqDto.getNewPassword()));

        return ResUserPwUpdateDto.fromEntity(userId);
    }

    @Override
    @Transactional
    public void deleteUser(HttpServletRequest request, Long userId, ReqUserDeleteDto reqDto) {
        String emailOfConnectingUser = AuthUtils.getUserEmail(request, jwtUtils);

        verifyAccessedUser(userId, emailOfConnectingUser);
        verifyPassword(reqDto.getPassword(), reqDto.getPassword2());

        userRepository.deleteById(userId);

        UserDeletedDomainEvent event = mapper.toUserDeletedEvent(userId);
        publisher.publishWithOutboxAfterCommit(event);
    }

    @Override
    @Transactional
    public ResLoginDto login(HttpServletResponse response, ReqLoginDto reqDto) throws Exception {
        LoginInfo loginInfo = cacheHelper.getLoginInfo(reqDto.getEmail());

        if (!passwordEncoder.matches(reqDto.getPassword(), loginInfo.getPassword())) {
            throw new BusinessException(ExceptionCode.BAD_CREDENTIALS);
        }

        if (loginInfo.getEmailVerifiedAt() == null) {
            throw new BusinessException(ExceptionCode.UNCERTIFIED_EMAIL);
        }

        String decodedEmail = AesUtils.aesCBCDecode(loginInfo.getEmail());

        createTokenAndSet(response, loginInfo.getId(), decodedEmail);

        return ResLoginDto.of(AesUtils.aesCBCDecode(loginInfo.getName()), decodedEmail);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtUtils.resolveToken(request.getHeader(JwtUtils.AUTHORIZATION));

        Claims claims = jwtUtils.getClaims(accessToken);
        if (claims == null) throw new BusinessException(ExceptionCode.EXPIRED_JWT);

        deleteTokenAndAddBlacklist(claims, response);
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
    @Transactional
    public ResEmailDto checkVerificationStr(Long userId, ReqEmailVerificationDto req) throws Exception {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));

        String decodedEmail = AesUtils.aesCBCDecode(savedUser.getEmail());
        String storedString = getVerificationNumber(decodedEmail);

        if (!storedString.equals(req.getVerificationCode())) throw new BusinessException(INVALID_EMAIL_VERIFICATION);

        savedUser.verifyEmail();
        return ResEmailDto.of(decodedEmail);
    }

    private void createTokenAndSet(HttpServletResponse response, Long userId, String decodedEmail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtUtils.USER_ID_CLAIM, userId);
        claims.put(JwtUtils.EMAIL_CLAIM, decodedEmail);

        String accessToken = jwtUtils.createAccessToken(claims);
        String refreshToken = jwtUtils.createRefreshToken();

        response.setHeader(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + accessToken);

        Cookie refreshCookie = new Cookie(JwtUtils.REFRESH_TOKEN_HEADER, refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) Duration.ofMillis(jwtUtils.getRefreshTokenExpirationPeriod()).getSeconds());
        response.addCookie(refreshCookie);

        redisService.setValues(
                decodedEmail, refreshToken, Duration.ofMillis(jwtUtils.getRefreshTokenExpirationPeriod())
        );
    }

    private void deleteTokenAndAddBlacklist(Claims claims, HttpServletResponse response) {
        String email = claims.get("email", String.class);
        String redisRefreshToken = redisService.getValues(email, String.class);
        long expireSeconds = jwtUtils.getRefreshTokenExpirationPeriod() / 1000;

        boolean swapped = redisService.swapToken(email, redisRefreshToken, expireSeconds);
        if (!swapped) throw new BusinessException(UNAUTHORIZED_ACCESS);

        Cookie refreshCookie = new Cookie(JwtUtils.REFRESH_TOKEN_HEADER, null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
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

    public void verifyExistingPassword(Long userId, String password) {
        if (!userRepository.existsByIdAndPassword(userId, passwordEncoder.encode(password))) {
            throw new BusinessException(ExceptionCode.INVALID_PASSWORD);
        }
    }

    public void verifyPassword(String password, String password2) {
        if (!password.equals(password2)) {
            throw new BusinessException(ExceptionCode.INVALID_PASSWORD);
        }
    }

    public void verifyAccessedUser(Long userId, String emailOfLoginUser) {
        if (userRepository.existsByIdAndEmail(userId, emailOfLoginUser)) {
            throw new BusinessException(ExceptionCode.NOT_FOUND_USER);
        }
    }

    private boolean checkRefreshToken(String refreshToken) {
        return StringUtils.hasText(refreshToken) &&
                jwtUtils.isTokenValid(refreshToken) &&
                getLogoutInfo(refreshToken);
    }

    private boolean getLogoutInfo(String refreshToken) {
        return redisService.getValues(refreshToken, String.class).equals("false");
    }

    private String getVerificationNumber(String email) {
        return redisService.getValues(email, String.class);
    }
}
