package com.purchase.hanghae99_user.email;

import com.purchase.hanghae99.common.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    @Async
    public void sendMail(String email) {
        String authNumber = createCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            mimeMessage.setFrom("preordertest13579@gmail.com");
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, email);
            mimeMessage.setSubject("이메일 인증");
            String string = "";
            string += "<h3> 요청하신 인증 번호입니다. </h3>";
            string += "<h1>" + authNumber + "</h3>";
            mimeMessage.setText(string, "UTF-8", "html");

            javaMailSender.send(mimeMessage);

            redisService.setValues(email, authNumber, Duration.ofMinutes(5));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97));
                case 1 -> key.append((char) (random.nextInt(26) + 65));
                default -> key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    public String getVerificationNumber(String email) {
        return redisService.getValues(email);
    }

    public boolean checkVerificationStr(String mail, String userStr) {
        String storedString = getVerificationNumber(mail);
        return storedString.equals(userStr);
    }
}
