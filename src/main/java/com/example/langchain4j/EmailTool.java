package com.example.langchain4j;

import dev.langchain4j.agent.tool.Tool;


import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailTool{

    @Tool("Send an email to a recipient with a subject and message")
    public String sendEmail(String recipient, String subject, String body){
        final String from = "[your mail id]";
        final String password = "[Your App Password]"; // Use environment variable or secure storage for production

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            System.out.println("Email sent successfully to " + recipient);
            return "Email sent successfully to " + recipient;
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            return "Failed to send email: " + e.getMessage();
        }

    }
}
