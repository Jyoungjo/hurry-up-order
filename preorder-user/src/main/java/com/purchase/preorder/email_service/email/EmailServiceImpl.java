package com.purchase.preorder.email_service.email;

import com.common.core.util.AuthCodeGenerator;
import com.purchase.preorder.user_service_common.util.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    public void send(String email) throws MessagingException {
        String authNumber = AuthCodeGenerator.generate();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        mimeMessage.setFrom("preordertest13579@gmail.com");
        mimeMessage.setRecipients(MimeMessage.RecipientType.TO, email);
        mimeMessage.setSubject("이메일 인증");

        String content = "<h3>요청하신 인증 번호입니다.</h3>" + "<h1>" + authNumber + "</h1>";

        mimeMessage.setText(content, "UTF-8", "html");

        javaMailSender.send(mimeMessage);

        redisService.setValues(email, authNumber, Duration.ofMinutes(5L));
    }
}
