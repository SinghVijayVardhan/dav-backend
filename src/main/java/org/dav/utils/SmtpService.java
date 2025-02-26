package org.dav.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.dav.entity.Loan;
import org.dav.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SmtpService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(User user, String subject, String textBody, Loan loan) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);

            String template = loadEmailTemplate();

            Map<String, String> replacements = new HashMap<>();
            replacements.put("{{userFirstName}}", user.getFirstname());
            replacements.put("{{textBody}}", textBody);

            if (loan != null && loan.getBook() != null) {
                replacements.put("{{loanDetails}}", "true");
                replacements.put("{{bookName}}", loan.getBook().getTitle());
                replacements.put("{{issueDate}}", loan.getIssueDate().toString());
                replacements.put("{{dueDate}}", loan.getDueDate().toString());
            } else {
                replacements.put("{{loanDetails}}", "false");
            }

            String htmlBody = replacePlaceholders(template, replacements);
            helper.setText(htmlBody, true);
            ClassPathResource backgroundImage = new ClassPathResource("static/dav.jpg");
            helper.addInline("backgroundImage", backgroundImage);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error sending mail: {}", e.getMessage());
        }
    }

    private String loadEmailTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-template.html");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error loading email template: {}", e.getMessage());
            return "";
        }
    }

    private String replacePlaceholders(String template, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }
        return template;
    }


}
