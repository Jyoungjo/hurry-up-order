package com.purchase.preorder.email_service.email.event.listener;

import com.common.core.exception.ExceptionCode;
import com.common.event_common.domain_event_vo.user.UserCreatedDomainEvent;
import com.common.web.exception.BusinessException;
import com.purchase.preorder.email_service.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailDomainEventListener {

    private final EmailService emailService;
    private final EmailDomainEventListenHelper helper;

    @Async(value = "businessEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(UserCreatedDomainEvent event) {
        helper.executeWithFailureHandling(event, () -> {
            try {
                emailService.send(event.getEmail());
            } catch (MessagingException e) {
                throw new BusinessException(ExceptionCode.INTERNAL_SERVER_ERROR);
            }
        });
    }
}
