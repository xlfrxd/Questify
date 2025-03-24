package com.example.questifyv1.activity;

import android.util.Log;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    private static final String TAG = "EmailSender";
    private final String username;
    private final String password;

    public EmailSender(String email, String password) {
        this.username = email;
        this.password = password;
    }

    public void sendEmail(String recipient, String subject, String body) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Attempting to send email to: " + recipient);

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                props.put("mail.smtp.timeout", "10000"); // 10 seconds
                props.put("mail.smtp.connectiontimeout", "10000");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        Log.d(TAG, "Authenticating...");
                        return new PasswordAuthentication(username, password);
                    }
                });

                // Enable debug logging
                session.setDebug(true);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                Log.d(TAG, "Email sent successfully!");

            } catch (AuthenticationFailedException e) {
                Log.e(TAG, "Authentication failed", e);
            } catch (MessagingException e) {
                Log.e(TAG, "Messaging error", e);
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error", e);
            }
        }).start();
    }}