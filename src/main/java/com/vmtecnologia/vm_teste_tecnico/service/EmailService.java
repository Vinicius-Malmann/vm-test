package com.vmtecnologia.vm_teste_tecnico.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String defaultFromEmail;
    private final EmailValidator emailValidator = EmailValidator.getInstance();

    public void sendEmail(String to, String subject, String body) {
        validateParameters(to, subject, body);
        send(createSimpleMessage(to, subject, body));
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        validateParameters(to, subject, htmlContent);
        send(createMimeMessage(to, subject, htmlContent));
    }

    private void validateParameters(String to, String subject, String content) {
        validateNotEmpty(to, "Recipient email address cannot be empty");
        validateNotEmpty(subject, "Email subject cannot be empty");
        validateNotEmpty(content, "Email content cannot be empty");
        validateEmailFormat(to);
    }

    private void validateNotEmpty(String value, String errorMessage) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateEmailFormat(String email) {
        if (!emailValidator.isValid(email)) {
            throw new IllegalArgumentException("Recipient email address is invalid");
        }
    }

    private SimpleMailMessage createSimpleMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(defaultFromEmail);
        return message;
    }

    private MimeMessage createMimeMessage(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(defaultFromEmail);
            return message;
        } catch (MessagingException e) {
            throw new EmailServiceException("Failed to create MIME message", e);
        }
    }

    private void send(Object message) {
        try {
            if (message instanceof SimpleMailMessage) {
                mailSender.send((SimpleMailMessage) message);
            } else if (message instanceof MimeMessage) {
                mailSender.send((MimeMessage) message);
            }
            log.info("Email successfully sent");
        } catch (MailException ex) {
            log.error("Failed to send email", ex);
            throw new EmailServiceException("Failed to send email", ex);
        }
    }

    public static class EmailServiceException extends RuntimeException {
        public EmailServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}