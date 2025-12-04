package com.example.fooddonation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.Base64;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("aeraf79@gmail.com");
        javaMailSender.send(mailMessage);
    }

    // ✅ NEW METHOD: Send email with image attachment
    @Async
    public void sendEmailWithAttachment(String toEmail, String subject, String message, 
                                       String base64Image, String imageName, String imageType) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(message);
            helper.setFrom("aeraf79@gmail.com");
            
            // Decode base64 image
            if (base64Image != null && !base64Image.isEmpty()) {
                // Remove data URL prefix if present (e.g., "data:image/png;base64,")
                String base64Data = base64Image;
                if (base64Image.contains(",")) {
                    base64Data = base64Image.split(",")[1];
                }
                
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                ByteArrayResource resource = new ByteArrayResource(imageBytes);
                
                // Attach image
                helper.addAttachment(imageName != null ? imageName : "proof.jpg", resource, imageType);
                System.out.println("✅ Image attached to email: " + imageName);
            }
            
            javaMailSender.send(mimeMessage);
            System.out.println("✅ Email with attachment sent to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email with attachment: " + e.getMessage());
            e.printStackTrace();
            // Fallback to plain text email
            sendEmail(toEmail, subject, message);
        }
    }
}