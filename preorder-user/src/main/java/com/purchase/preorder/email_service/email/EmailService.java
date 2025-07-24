package com.purchase.preorder.email_service.email;

import jakarta.mail.MessagingException;

public interface EmailService {
    void send(String email) throws MessagingException;
}
