package com.purchase.preorder.user_service.user.service;

import com.common.domain.entity.user.User;
import com.purchase.preorder.email_service.email.ResEmailDto;
import com.purchase.preorder.user_service.user.dto.create.ReqUserCreateDto;
import com.purchase.preorder.user_service.user.dto.create.ResUserCreateDto;
import com.purchase.preorder.user_service.user.dto.delete.ReqUserDeleteDto;
import com.purchase.preorder.user_service.user.dto.login.ReqLoginDto;
import com.purchase.preorder.user_service.user.dto.login.ResLoginDto;
import com.purchase.preorder.user_service.user.dto.read.ResUserInfoDto;
import com.purchase.preorder.user_service.user.dto.update.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    ResUserCreateDto createUser(ReqUserCreateDto reqDto) throws Exception;
    ResUserInfoDto readUser(Long userId);
    ResUserUpdateDto updateUserInfo(HttpServletRequest request, Long userId, ReqUserInfoUpdateDto reqDto) throws Exception;
    ResUserPwUpdateDto updateUserPassword(HttpServletRequest request, Long userId, ReqUserPasswordUpdateDto reqDto) throws Exception;
    void deleteUser(HttpServletRequest request, Long userId, ReqUserDeleteDto reqDto) throws Exception;
    ResLoginDto login(HttpServletResponse response, ReqLoginDto reqDto) throws Exception;
    void logout(HttpServletRequest request, HttpServletResponse response);
    User findUserByEmail(String email);
    void reissue(HttpServletRequest request, HttpServletResponse response);
    ResEmailDto checkVerificationStr(Long userId, ReqEmailVerificationDto req) throws Exception;
}
