package com.purchase.hanghae99.user;

import com.purchase.hanghae99.user.dto.create.ReqUserCreateDto;
import com.purchase.hanghae99.user.dto.create.ResUserCreateDto;
import com.purchase.hanghae99.user.dto.delete.ReqUserDeleteDto;
import com.purchase.hanghae99.user.dto.read.ResUserInfoDto;
import com.purchase.hanghae99.user.dto.update.ReqUserInfoUpdateDto;
import com.purchase.hanghae99.user.dto.update.ReqUserPasswordUpdateDto;
import com.purchase.hanghae99.user.dto.update.ResUserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResUserCreateDto createUser(ReqUserCreateDto reqDto) {
        checkDuplicateEmail(reqDto.getEmail());
        checkDuplicatePhoneNumber(reqDto.getPhoneNumber());

        User user = reqDto.toEntity(passwordEncoder);

        // TODO: 이메일 인증 발송 로직

        return ResUserCreateDto.fromEntity(userRepository.save(user));
    }

    @Override
    public ResUserInfoDto readUser(Long userId) {
        return userRepository.findById(userId)
                .filter(x -> x.getRole().equals(UserRole.CERTIFIED_USER))
                .map(ResUserInfoDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Override
    @Transactional
    public ResUserUpdateDto updateUserInfo(Long userId, ReqUserInfoUpdateDto reqDto) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        savedUser.updateUserInfo(reqDto.getPhoneNumber(), reqDto.getAddress());
        return ResUserUpdateDto.fromEntity(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public ResUserUpdateDto updateUserPassword(Long userId, ReqUserPasswordUpdateDto reqDto) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        verifyExistingPassword(reqDto.getOriginalPassword(), savedUser.getPassword());
        verifyPassword(reqDto.getNewPassword(), reqDto.getNewPassword2());

        savedUser.updatePassword(passwordEncoder.encode(reqDto.getNewPassword()));
        return ResUserUpdateDto.fromEntity(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, ReqUserDeleteDto reqDto) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        verifyPassword(reqDto.getPassword(), reqDto.getPassword2());

        userRepository.delete(savedUser);
    }

    private void checkDuplicateEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일 입니다.");
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber) {
        if (!userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("이미 등록된 전화번호 입니다.");
        }
    }

    private void verifyExistingPassword(String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, newPassword)) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
    }

    private void verifyPassword(String password, String password2) {
        if (!password.equals(password2)) {
            throw new IllegalArgumentException("입력하신 비밀번호가 일치하지 않습니다.");
        }
    }
}
