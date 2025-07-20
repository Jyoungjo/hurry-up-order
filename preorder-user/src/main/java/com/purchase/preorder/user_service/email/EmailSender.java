package com.purchase.preorder.user_service.email;

import com.common.core.util.AuthCodeGenerator;
import com.purchase.preorder.user_service.common.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    @Async
    public void sendMail(String email) {
        String authNumber = AuthCodeGenerator.generate();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            mimeMessage.setFrom("preordertest13579@gmail.com");
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, email);
            mimeMessage.setSubject("이메일 인증");

            String content = "<h3>요청하신 인증 번호입니다.</h3>" + "<h1>" + authNumber + "</h1>";

            mimeMessage.setText(content, "UTF-8", "html");

            javaMailSender.send(mimeMessage);

            redisService.setValues(email, authNumber, Duration.ofMinutes(5L));
            log.info("이메일 전송 완료 - email: {}", email);
        } catch (MessagingException e) {
            log.warn("이메일 전송 실패 - email: {}, error: {}", email, e.getMessage());
        }
    }
}
