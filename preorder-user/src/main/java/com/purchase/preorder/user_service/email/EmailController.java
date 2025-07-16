package com.purchase.preorder.user_service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping
    public ResEmailDto sendMail(String email) {
        try {
            emailService.sendMail(email);
            return EmailDtoFactory.succeed();
        } catch (Exception e) {
            return EmailDtoFactory.fail();
        }
    }

    @GetMapping
    public ResponseEntity<?> checkMail(@RequestParam String email, @RequestParam String userStr) {
        return ResponseEntity.ok(emailService.checkVerificationStr(email, userStr));
    }
}
