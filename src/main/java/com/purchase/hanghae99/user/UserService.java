package com.purchase.hanghae99.user;

import com.purchase.hanghae99.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserUpdateDto;

public interface UserService {
    ResUserCreateDto createUser(ReqUserCreateDto reqDto);
    ResUserInfoDto readUser(Long userId);
    ResUserUpdateDto updateUserInfo(Long userId, ReqUserInfoUpdateDto reqDto);
    ResUserUpdateDto updateUserPassword(Long userId, ReqUserPasswordUpdateDto reqDto);
    void deleteUser(Long userId, ReqUserDeleteDto reqDto);
}
