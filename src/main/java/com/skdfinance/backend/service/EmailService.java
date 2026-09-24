package com.skdfinance.backend.service;

import com.skdfinance.backend.entity.Lead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * A failed email must NEVER break the actual business operation (the lead is
 * already saved to the database before this is ever called) — so every send
 * is wrapped in a try/catch that just logs and moves on.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.admin-email}")
    private String adminEmail;

    public void sendNewLeadNotification(Lead lead) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("New enquiry — " + lead.getServiceInterested());
            message.setText(
                    "A new enquiry has come in through the website:\n\n" +
                            "Name: " + lead.getFullName() + "\n" +
                            "Phone: " + lead.getPhone() + "\n" +
                            "Email: " + (lead.getEmail() != null ? lead.getEmail() : "-") + "\n" +
                            "Service interested in: " + lead.getServiceInterested() + "\n" +
                            "Message: " + (lead.getMessage() != null ? lead.getMessage() : "-") + "\n\n" +
                            "View it in the admin dashboard to respond."
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send new-lead email notification for lead id {}: {}",
                    lead.getId(), e.getMessage());
        }
    }
}