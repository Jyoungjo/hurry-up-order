package com.purchase.preorder.user;

import com.purchase.preorder.email.ResEmailDto;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    ResUserCreateDto createUser(ReqUserCreateDto reqDto) throws Exception;
    ResUserInfoDto readUser(Long userId);
    ResUserUpdateDto updateUserInfo(HttpServletRequest request, Long userId, ReqUserInfoUpdateDto reqDto) throws Exception;
    ResUserPwUpdateDto updateUserPassword(HttpServletRequest request, Long userId, ReqUserPasswordUpdateDto reqDto) throws Exception;
    ResEmailDto updateEmailVerification(Long userId, String userStr) throws Exception;
    void deleteUser(HttpServletRequest request, Long userId, ReqUserDeleteDto reqDto) throws Exception;
    ResLoginDto login(HttpServletResponse response, ReqLoginDto reqDto) throws Exception;
    void logout(HttpServletRequest request, HttpServletResponse response);
    User findUserByEmail(String email);
    void reissue(HttpServletRequest request, HttpServletResponse response);
}
