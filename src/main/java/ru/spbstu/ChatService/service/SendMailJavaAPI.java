package ru.spbstu.ChatService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class SendMailJavaAPI {

    @Value("${plintum.chatservice.email.from}")
    private String emailFrom;

    public void sendMail(String emailTo, String subject,  String message) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailFrom));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            msg.setSubject(subject);
            msg.setText(message);
            System.out.println("Email sent successfully!");
        }

        catch (AddressException err) {
            throw new RuntimeException(err);
        } catch (MessagingException err) {
            throw new RuntimeException(err);
        }
    }
}
