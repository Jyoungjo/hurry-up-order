package com.purchase.hanghae99.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purchase.hanghae99.common.exception.BusinessException;
import com.purchase.hanghae99.common.security.JwtAuthFilter;
import com.purchase.hanghae99.email.ResEmailDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.purchase.hanghae99.common.exception.ExceptionCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthFilter.class
        })
})
@AutoConfigureRestDocs
@WithMockUser(roles = "USER")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // CREATE
    @DisplayName("회원가입 확인")
    @Test
    void register() throws Exception {
        // given
        ReqUserCreateDto req = new ReqUserCreateDto(
                "test@email.com", "a12345678!", "이름1", "주소1", "010-1234-5678"
        );

        ResUserCreateDto res = new ResUserCreateDto(
                1L, "이름1", "test@email.com"
        );

        // when
        when(userService.createUser(any())).thenReturn(res);

        // then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("user/회원가입/성공"));
    }

    // CREATE
    @DisplayName("회원가입 실패 - 필수 입력 값 누락")
    @Test
    void registerFailMissingFields() throws Exception {
        // given
        ReqUserCreateDto req = new ReqUserCreateDto(
                "", "a12345678!", "이름1", "주소1", "010-1234-5678"
        );

        // when
        when(userService.createUser(any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("user/회원가입/실패/필수_입력값_누락"));
    }
    
    // CREATE
    @DisplayName("회원가입 실패 - 이메일 형식 오류")
    @Test
    void registerFailInvalidEmail() throws Exception {
        // given
        ReqUserCreateDto req = new ReqUserCreateDto(
                "invalid-email", "a12345678!", "이름1", "주소1", "010-1234-5678"
        );

        // when
        when(userService.createUser(any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("user/회원가입/실패/이메일_형식_오류"));
    }

    // CREATE
    @DisplayName("회원가입 실패 - 중복 이메일")
    @Test
    void registerFailAlreadyExistEmail() throws Exception {
        // given
        ReqUserCreateDto req = new ReqUserCreateDto(
                "test@email.com", "a12345678!", "이름1", "주소1", "010-1234-5678"
        );

        // when
        when(userService.createUser(any())).thenThrow(new BusinessException(ALREADY_REGISTERED_EMAIL));

        // then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ALREADY_REGISTERED_EMAIL.getMessage()))
                .andDo(print())
                .andDo(document("user/회원가입/실패/중복_이메일"));
    }

    // CREATE
    @DisplayName("회원가입 실패 - 중복 전화번호")
    @Test
    void registerFailAlreadyExistPhoneNumber() throws Exception {
        // given
        ReqUserCreateDto req = new ReqUserCreateDto(
                "test1@email.com", "a12345678!", "이름1", "주소1", "010-1234-5678"
        );

        // when
        when(userService.createUser(any())).thenThrow(new BusinessException(ALREADY_REGISTERED_PHONE_NUMBER));

        // then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ALREADY_REGISTERED_PHONE_NUMBER.getMessage()))
                .andDo(print())
                .andDo(document("user/회원가입/실패/중복_전화번호"));
    }

    // READ
    @DisplayName("유저 조회 확인")
    @Test
    void findUser() throws Exception {
        // given
        long userId = 1L;

        ResUserInfoDto res = new ResUserInfoDto(
                userId, "이름1", "test@email.com", "주소1", "010-1234-5678"
        );

        // when
        when(userService.readUser(anyLong())).thenReturn(res);

        // then
        mockMvc.perform(get("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/유저_조회/성공"));
    }

    // READ
    @DisplayName("유저 조회 실패 - 존재하지 않는 유저")
    @Test
    void findUserFailNotFound() throws Exception {
        // given
        long userId = 1L;

        // when
        when(userService.readUser(anyLong())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(get("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_조회/실패/존재하지_않는_유저"));
    }

    // UPDATE INFO
    @DisplayName("유저 정보 수정 확인")
    @Test
    void updateUserInfo() throws Exception {
        // given
        long userId = 1L;

        ReqUserInfoUpdateDto req = new ReqUserInfoUpdateDto(
                "주소2", "010-1234-5679"
        );

        ResUserUpdateDto res = new ResUserUpdateDto(
                userId, "주소2", "010-1234-5679"
        );

        // when
        when(userService.updateUserInfo(anyLong(), any())).thenReturn(res);

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/유저_정보_수정"));
    }

    // UPDATE INFO
    @DisplayName("유저 정보 수정 실패 - 존재하지 않는 유저")
    @Test
    void updateUserInfoFailNotFound() throws Exception {
        // given
        long userId = 1L;

        ReqUserInfoUpdateDto req = new ReqUserInfoUpdateDto(
                "주소2", "010-1234-5679"
        );

        // when
        when(userService.updateUserInfo(anyLong(), any())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_정보_수정/실패/존재하지_않는_유저"));
    }

    // UPDATE INFO
    @DisplayName("유저 정보 수정 실패 - 필수 입력값 누락")
    @Test
    void updateUserInfoFailMissingFields() throws Exception {
        // given
        long userId = 1L;

        ReqUserInfoUpdateDto req = new ReqUserInfoUpdateDto(
                "", "010-1234-5679"
        );

        // when
        when(userService.updateUserInfo(anyLong(), any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_정보_수정/실패/필수_입력값_누락"));
    }

    // UPDATE INFO
    @DisplayName("유저 정보 수정 실패 - 올바르지 않은 전화번호 형식")
    @Test
    void updateUserInfoFailInvalidPhoneNumber() throws Exception {
        // given
        long userId = 1L;

        ReqUserInfoUpdateDto req = new ReqUserInfoUpdateDto(
                "주소1", "010123548945"
        );

        // when
        when(userService.updateUserInfo(anyLong(), any())).thenThrow(new BusinessException(INVALID_INPUT_VALUE));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_정보_수정/실패/올바르지_않은_전화번호_형식"));
    }

    // UPDATE PASSWORD
    @DisplayName("유저 비밀번호 수정 확인")
    @Test
    void updateUserPassword() throws Exception {
        // given
        long userId = 1L;

        ReqUserPasswordUpdateDto req = new ReqUserPasswordUpdateDto(
                "a12345678!", "asd123456!@", "asd123456!@"
        );

        ResUserPwUpdateDto res = new ResUserPwUpdateDto(
                userId, "이름1", "test@email.com"
        );

        // when
        when(userService.updateUserPassword(anyLong(), any())).thenReturn(res);

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/유저_비밀번호_수정/성공"));
    }

    // UPDATE PASSWORD
    @DisplayName("유저 비밀번호 수정 실패 - 존재하지 않는 유저")
    @Test
    void updateUserPasswordFailNotFound() throws Exception {
        // given
        long userId = 1L;

        ReqUserPasswordUpdateDto req = new ReqUserPasswordUpdateDto(
                "a12345678!", "asd123456!@", "asd123456!@"
        );

        // when
        when(userService.updateUserPassword(anyLong(), any())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_비밀번호_수정/실패/존재하지_않는_유저"));
    }

    // UPDATE PASSWORD
    @DisplayName("유저 비밀번호 수정 실패 - 현재 비밀번호와 입력한 비밀번호가 일치하지 않음")
    @Test
    void updateUserPasswordFailNotMatches() throws Exception {
        // given
        long userId = 1L;

        ReqUserPasswordUpdateDto req = new ReqUserPasswordUpdateDto(
                "a12345689@", "asd123456!@", "asd123456!@"
        );

        // when
        when(userService.updateUserPassword(anyLong(), any())).thenThrow(new BusinessException(INVALID_PASSWORD));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(INVALID_PASSWORD.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_비밀번호_수정/실패/기존_비밀번호_불일치"));
    }

    // UPDATE PASSWORD
    @DisplayName("유저 비밀번호 수정 실패 - 새로운 비밀번호와 그 확인이 일치하지 않음")
    @Test
    void updateUserPasswordFailNotMatchesNew() throws Exception {
        // given
        long userId = 1L;

        ReqUserPasswordUpdateDto req = new ReqUserPasswordUpdateDto(
                "a12345678!", "asd123456!@", "asd123456!!"
        );

        // when
        when(userService.updateUserPassword(anyLong(), any())).thenThrow(new BusinessException(INVALID_PASSWORD));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(INVALID_PASSWORD.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_비밀번호_수정/실패/새로운_비밀번호_불일치"));
    }

    // UPDATE EMAIL VERIFICATION
    @DisplayName("유저 이메일 인증 확인")
    @Test
    void updateUserEmailVerification() throws Exception {
        // given
        long userId = 1L;

        String userStr = "853i135B";

        ResEmailDto res = new ResEmailDto(
                true, "인증 완료"
        );

        // when
        when(userService.updateEmailVerification(anyLong(), anyString())).thenReturn(res);

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userStr", userStr)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/유저_이메일_인증/성공"));
    }

    // UPDATE EMAIL VERIFICATION
    @DisplayName("유저 이메일 인증 실패 - 존재하지 않는 유저")
    @Test
    void updateUserEmailVerificationFailNotFound() throws Exception {
        // given
        long userId = 1L;

        String userStr = "853i135B";

        // when
        when(userService.updateEmailVerification(anyLong(), anyString()))
                .thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userStr", userStr)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_이메일_인증/실패/존재하지_않는_유저"));
    }

    // UPDATE EMAIL VERIFICATION
    @DisplayName("유저 이메일 인증 실패 - 인증번호 불일치")
    @Test
    void updateUserEmailVerificationFailInvalidNumber() throws Exception {
        // given
        long userId = 1L;

        String userStr = "853i135B";

        // when
        when(userService.updateEmailVerification(anyLong(), anyString()))
                .thenThrow(new BusinessException(INVALID_VERIFICATION_NUMBER));

        // then
        mockMvc.perform(put("/api/v1/users/" + userId + "/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userStr", userStr)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(INVALID_VERIFICATION_NUMBER.getMessage()))
                .andDo(print())
                .andDo(document("user/유저_이메일_인증/실패/인증번호_불일치"));
    }

    // DELETE
    @DisplayName("회원 탈퇴 확인")
    @Test
    void deleteUser() throws Exception {
        // given
        long userId = 1L;

        ReqUserDeleteDto req = new ReqUserDeleteDto(
                "a12345678!", "a12345678!"
        );

        // when
        doNothing().when(userService).deleteUser(any(), anyLong(), any());

        // then
        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("user/회원_탈퇴/성공"));
    }

    // DELETE
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 유저")
    @Test
    void deleteUserFailNotFound() throws Exception {
        // given
        long userId = 1L;

        ReqUserDeleteDto req = new ReqUserDeleteDto(
                "a12345678!", "a12345678!"
        );

        // when
        doThrow(new BusinessException(NOT_FOUND_USER)).when(userService).deleteUser(any(), anyLong(), any());

        // then
        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("user/회원_탈퇴/실패/존재하지_않는_유저"));
    }

    // DELETE
    @DisplayName("회원 탈퇴 실패 - 인증되지 않은 유저가 삭제를 시도하면 실패")
    @Test
    void deleteUserFailUnauthorized() throws Exception {
        // given
        long userId = 1L;

        ReqUserDeleteDto req = new ReqUserDeleteDto(
                "a12345678!", "a12345678!"
        );

        // when
        doThrow(new BusinessException(UNAUTHORIZED_ACCESS)).when(userService).deleteUser(any(), anyLong(), any());

        // then
        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("user/회원_탈퇴/실패/미인증_유저의_삭제_시도"));
    }

    // DELETE
    @DisplayName("회원 탈퇴 실패 - 접속한 유저가 삭제하지 않으면 실패")
    @Test
    void deleteUserFailNotMatchAccessUser() throws Exception {
        // given
        long userId = 1L;

        ReqUserDeleteDto req = new ReqUserDeleteDto(
                "a12345678!", "a12345678!"
        );

        // when
        doThrow(new BusinessException(UNAUTHORIZED_ACCESS)).when(userService).deleteUser(any(), anyLong(), any());

        // then
        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(print())
                .andDo(document("user/회원_탈퇴/실패/접속_유저_불일치"));
    }

    // DELETE
    @DisplayName("회원 탈퇴 실패 - 비밀번호 확인 실패")
    @Test
    void deleteUserFailNotMatchPassword() throws Exception {
        // given
        long userId = 1L;

        ReqUserDeleteDto req = new ReqUserDeleteDto(
                "a12345678!", "a12345678!"
        );

        // when
        doThrow(new BusinessException(INVALID_PASSWORD)).when(userService).deleteUser(any(), anyLong(), any());

        // then
        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(INVALID_PASSWORD.getMessage()))
                .andDo(print())
                .andDo(document("user/회원_탈퇴/실패/비밀번호_확인_실패"));
    }

    // LOGIN
    @DisplayName("로그인 확인")
    @Test
    void login() throws Exception {
        // given
        ReqLoginDto req = new ReqLoginDto(
                "test@email.com", "a12345678!"
        );

        ResLoginDto res = new ResLoginDto(
                "이름1", "test@email.com", "로그인 성공"
        );

        // when
        when(userService.login(any(), any())).thenReturn(res);

        // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/로그인/성공"));
    }

    // LOGIN
    @DisplayName("로그인 실패 - 존재하지 않는 유저")
    @Test
    void loginFailNotFound() throws Exception {
        // given
        ReqLoginDto req = new ReqLoginDto(
                "test@email.com", "a12345678!"
        );

        // when
        when(userService.login(any(), any())).thenThrow(new BusinessException(NOT_FOUND_USER));

        // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_USER.getMessage()))
                .andDo(print())
                .andDo(document("user/로그인/실패/존재하지_않는_유저"));
    }

    // LOGIN
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    void loginFailNotMatchPassword() throws Exception {
        // given
        ReqLoginDto req = new ReqLoginDto(
                "test@email.com", "a12345679!"
        );

        // when
        when(userService.login(any(), any())).thenThrow(new BusinessException(BAD_CREDENTIALS));

        // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(BAD_CREDENTIALS.getMessage()))
                .andDo(print())
                .andDo(document("user/로그인/실패/비밀번호_불일치"));
    }

    // LOGIN
    @DisplayName("로그인 실패 - 이메일 미인증 유저")
    @Test
    void loginFailUncertifiedEmail() throws Exception {
        // given
        ReqLoginDto req = new ReqLoginDto(
                "test@email.com", "a12345678!"
        );

        // when
        when(userService.login(any(), any())).thenThrow(new BusinessException(UNCERTIFIED_EMAIL));

        // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(UNCERTIFIED_EMAIL.getMessage()))
                .andDo(print())
                .andDo(document("user/로그인/실패/이메일_미인증_유저"));
    }

    // LOGOUT
    @DisplayName("로그아웃 확인")
    @Test
    void logout() throws Exception {
        // given

        // when
        doNothing().when(userService).logout(any(), any());

        // then
        mockMvc.perform(post("/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("user/로그아웃/성공"));
    }

    // LOGOUT
    @DisplayName("로그아웃 실패 - 토큰 만료")
    @Test
    void logoutFailNotFound() throws Exception {
        // given

        // when
        doThrow(new BusinessException(EXPIRED_JWT)).when(userService).logout(any(), any());

        // then
        mockMvc.perform(post("/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(EXPIRED_JWT.getMessage()))
                .andDo(print())
                .andDo(document("user/로그아웃/실패/토큰_만료"));
    }
}