package com.purchase.hanghae99.user;

import com.purchase.hanghae99.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResUserCreateDto> register(@Valid @RequestBody ReqUserCreateDto reqDto) {
        return ResponseEntity.status(CREATED).body(userService.createUser(reqDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResUserInfoDto> findUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.readUser(userId));
    }

    @PutMapping("/{userId}/info")
    public ResponseEntity<ResUserUpdateDto> updateUserInfo(
            @PathVariable Long userId, @Valid @RequestBody ReqUserInfoUpdateDto reqDto
    ) {
        return ResponseEntity.ok(userService.updateUserInfo(userId, reqDto));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ResUserUpdateDto> updateUserPassword(
            @PathVariable Long userId, @Valid @RequestBody ReqUserPasswordUpdateDto reqDto
    ) {
        return ResponseEntity.ok(userService.updateUserPassword(userId, reqDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId, @Valid @RequestBody ReqUserDeleteDto reqDto
    ) {
        userService.deleteUser(userId, reqDto);
        return ResponseEntity.noContent().build();
    }
}
