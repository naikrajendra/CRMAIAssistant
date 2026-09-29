package com.crmai.assistant.service;

import com.crmai.assistant.model.DailyBriefing;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailDispatchService {

    private static final Logger log = LoggerFactory.getLogger(EmailDispatchService.class);

    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;

    public EmailDispatchService(JavaMailSender mailSender, EmailTemplateService emailTemplateService) {
        this.mailSender = mailSender;
        this.emailTemplateService = emailTemplateService;
    }

    public void sendEmail(DailyBriefing briefing) {
        if (mailSender == null) {
            log.info("Email sending skipped because JavaMailSender is not configured. Account: {}",
                    briefing.accountName());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@crm-intelligence.local");
            helper.setTo("account-owner@company.local");
            helper.setSubject(briefing.subject());
            helper.setText(emailTemplateService.renderHtmlEmail(briefing), true);
            mailSender.send(message);
            log.info("Daily briefing email sent for {} with confidence {}", briefing.accountName(),
                    briefing.confidenceScore());
        } catch (MessagingException | MailException e) {
            log.warn("Unable to send briefing email for {}: {}", briefing.accountName(), e.getMessage());
        }
    }
}
