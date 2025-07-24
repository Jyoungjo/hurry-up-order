package com.purchase.preorder.user_service.user.service;

import com.common.core.exception.ExceptionCode;
import com.common.core.util.AesUtils;
import com.common.domain.entity.user.projection.LoginInfo;
import com.common.domain.repository.user.UserRepository;
import com.common.web.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserCacheHelper {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cacheable(key = "#email", value = "loginInfo", unless = "#result == null")
    public LoginInfo getLoginInfo(String email) throws Exception {
        return userRepository.findLoginInfoByEmail(AesUtils.aesCBCEncode(email))
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND_USER));
    }
}
