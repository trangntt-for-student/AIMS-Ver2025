package com.hust.soict.aims.services.notification;

import com.hust.soict.aims.utils.ConfigLoader;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailNotification implements INotification {
    
    private final String smtpHost;
    private final String smtpPort;
    private final String senderEmail;
    private final String senderPassword;
    private final boolean enableTls;
    
    public MailNotification() {
        this.smtpHost = ConfigLoader.getProperty("mail.smtp.host", "smtp.gmail.com");
        this.smtpPort = ConfigLoader.getProperty("mail.smtp.port", "587");
        this.senderEmail = ConfigLoader.getProperty("mail.sender.email");
        this.senderPassword = ConfigLoader.getProperty("mail.sender.password");
        this.enableTls = Boolean.parseBoolean(
            ConfigLoader.getProperty("mail.smtp.starttls.enable", "true")
        );
    }
    
    @Override
    public void notify(NotificationMessage message) {
        if (senderEmail == null || senderPassword == null) {
            System.err.println("[MailNotification] Email credentials not configured. Skipping email.");
            logMessage(message);
            return;
        }
        
        try {
            sendEmail(message);
            System.out.println("[MailNotification] Email sent successfully to: " + message.getRecipient());
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("[MailNotification] Failed to send email: " + e.getMessage());
            logMessage(message);
        }
    }
    
    private void sendEmail(NotificationMessage message) throws MessagingException, UnsupportedEncodingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(enableTls));
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        
        Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(senderEmail, "AIMS Store"));
        mimeMessage.setRecipients(Message.RecipientType.TO, 
            InternetAddress.parse(message.getRecipient()));
        mimeMessage.setSubject(message.getSubject());
        mimeMessage.setContent(message.getContent(), "text/html; charset=UTF-8");
        
        Transport.send(mimeMessage);
    }
    
    private void logMessage(NotificationMessage message) {
        System.out.println("=== Email Notification (logged) ===");
        System.out.println("To: " + message.getRecipient());
        System.out.println("Subject: " + message.getSubject());
        System.out.println("Content: " + message.getContent());
        System.out.println("===================================");
    }
}
