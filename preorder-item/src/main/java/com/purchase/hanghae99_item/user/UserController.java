package com.purchase.hanghae99_item.user;

import com.purchase.hanghae99_item.email.ResEmailDto;
import com.purchase.hanghae99_item.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99_item.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99_item.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99_item.user.dto.login.ReqLoginDto;
import com.purchase.hanghae99_item.user.dto.login.ResLoginDto;
import com.purchase.hanghae99_item.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99_item.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99_item.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99_item.user.dto.update.ResUserPwUpdateDto;
import com.purchase.hanghae99_item.user.dto.update.ResUserUpdateDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResUserCreateDto> register(@Valid @RequestBody ReqUserCreateDto reqDto) throws Exception {
        return ResponseEntity.status(CREATED).body(userService.createUser(reqDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResUserInfoDto> findUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.readUser(userId));
    }

    @PutMapping("/{userId}/info")
    public ResponseEntity<ResUserUpdateDto> updateUserInfo(
            @PathVariable("userId") Long userId, @Valid @RequestBody ReqUserInfoUpdateDto reqDto
    ) throws Exception {
        return ResponseEntity.ok(userService.updateUserInfo(userId, reqDto));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ResUserPwUpdateDto> updateUserPassword(
            @PathVariable("userId") Long userId, @Valid @RequestBody ReqUserPasswordUpdateDto reqDto
    ) {
        return ResponseEntity.ok(userService.updateUserPassword(userId, reqDto));
    }

    @PutMapping("/{userId}/email-verification")
    public ResponseEntity<ResEmailDto> updateUserEmailVerification(
            @PathVariable("userId") Long userId, @RequestParam("userStr") String userStr
    ) throws Exception {
        return ResponseEntity.ok(userService.updateEmailVerification(userId, userStr));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            Authentication authentication, @PathVariable("userId") Long userId, @Valid @RequestBody ReqUserDeleteDto reqDto
    ) throws Exception {
        userService.deleteUser(authentication, userId, reqDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDto> login(
            HttpServletResponse response, @Valid @RequestBody ReqLoginDto reqLoginDto
    ) throws Exception {
        return ResponseEntity.ok(userService.login(response, reqLoginDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request, HttpServletResponse response
    ) {
        userService.logout(request, response);
        return ResponseEntity.noContent().build();
    }
}
