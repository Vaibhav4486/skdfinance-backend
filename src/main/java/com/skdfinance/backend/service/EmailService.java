package com.skdfinance.backend.service;

import com.skdfinance.backend.entity.Lead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sends email via Resend's HTTP API instead of raw SMTP.
 *
 * Why: Render (and most free hosting tiers) block outbound SMTP connections
 * on ports 25/465/587 to prevent their infrastructure being used for spam.
 * JavaMailSender's connection to smtp.gmail.com will time out there every
 * single time, with no credentials fix possible — the connection never even
 * reaches Gmail. An HTTP API call over port 443 (regular HTTPS) sidesteps
 * that restriction entirely.
 *
 * A failed send is logged and swallowed, same as before — a broken email
 * integration must never break the actual lead submission, which is already
 * saved to the database by the time this runs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final RestTemplate restTemplate;

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${resend.from-address}")
    private String fromAddress;

    @Value("${notification.admin-email}")
    private String adminEmail;

    public void sendNewLeadNotification(Lead lead) {
        try {
            String text =
                    "A new enquiry has come in through the website:\n\n" +
                            "Name: " + lead.getFullName() + "\n" +
                            "Phone: " + lead.getPhone() + "\n" +
                            "Email: " + (lead.getEmail() != null ? lead.getEmail() : "-") + "\n" +
                            "Service interested in: " + lead.getServiceInterested() + "\n" +
                            "Message: " + (lead.getMessage() != null ? lead.getMessage() : "-") + "\n\n" +
                            "View it in the admin dashboard to respond.";

            Map<String, Object> body = new HashMap<>();
            body.put("from", fromAddress);
            body.put("to", List.of(adminEmail));
            body.put("subject", "New enquiry — " + lead.getServiceInterested());
            body.put("text", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);

        } catch (Exception e) {
            log.warn("Failed to send new-lead email notification for lead id {}: {}",
                    lead.getId(), e.getMessage());
        }
    }
}