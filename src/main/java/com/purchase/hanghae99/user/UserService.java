package com.purchase.hanghae99.user;

import com.purchase.hanghae99.email.ResEmailDto;
import com.purchase.hanghae99.user.dto.login.ReqLoginDto;
import com.purchase.hanghae99.user.dto.login.ResLoginDto;
import com.purchase.hanghae99.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserPwUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserUpdateDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    ResUserCreateDto createUser(ReqUserCreateDto reqDto);
    ResUserInfoDto readUser(Long userId);
    ResUserUpdateDto updateUserInfo(Long userId, ReqUserInfoUpdateDto reqDto);
    ResUserPwUpdateDto updateUserPassword(Long userId, ReqUserPasswordUpdateDto reqDto);
    ResEmailDto updateEmailVerification(Long userId, String userStr);
    void deleteUser(Long userId, ReqUserDeleteDto reqDto);
    ResLoginDto login(HttpServletResponse response, ReqLoginDto reqDto);
    void logout(HttpServletRequest request, HttpServletResponse response);
}
