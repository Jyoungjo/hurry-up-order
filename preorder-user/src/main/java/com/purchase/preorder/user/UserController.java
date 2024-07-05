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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service/api/v1")
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ResUserCreateDto> register(@Valid @RequestBody ReqUserCreateDto reqDto) throws Exception {
        return ResponseEntity.status(CREATED).body(userService.createUser(reqDto));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResUserInfoDto> findUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.readUser(userId));
    }

    @PutMapping("/users/{userId}/info")
    public ResponseEntity<ResUserUpdateDto> updateUserInfo(
            HttpServletRequest request,
            @PathVariable("userId") Long userId,
            @Valid @RequestBody ReqUserInfoUpdateDto reqDto
    ) throws Exception {
        return ResponseEntity.ok(userService.updateUserInfo(request, userId, reqDto));
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<ResUserPwUpdateDto> updateUserPassword(
            HttpServletRequest request,
            @PathVariable("userId") Long userId,
            @Valid @RequestBody ReqUserPasswordUpdateDto reqDto
    ) throws Exception {
        return ResponseEntity.ok(userService.updateUserPassword(request, userId, reqDto));
    }

    @PutMapping("/users/{userId}/email-verification")
    public ResponseEntity<ResEmailDto> updateUserEmailVerification(
            @PathVariable("userId") Long userId, @RequestParam("userStr") String userStr
    ) throws Exception {
        return ResponseEntity.ok(userService.updateEmailVerification(userId, userStr));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            HttpServletRequest request, @PathVariable("userId") Long userId, @Valid @RequestBody ReqUserDeleteDto reqDto
    ) throws Exception {
        userService.deleteUser(request, userId, reqDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/login")
    public ResponseEntity<ResLoginDto> login(
            HttpServletResponse response, @Valid @RequestBody ReqLoginDto reqLoginDto
    ) throws Exception {
        return ResponseEntity.ok(userService.login(response, reqLoginDto));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request, HttpServletResponse response
    ) {
        userService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/users")
    public ResponseEntity<User> getUser(@RequestParam("email") String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    @PostMapping("/users/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        userService.reissue(request, response);
        return ResponseEntity.noContent().build();
    }
}
