package ru.spbstu.ChatService.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.spbstu.ChatService.domain.Invitation;
import ru.spbstu.ChatService.domain.Session;
import ru.spbstu.ChatService.model.ChatMessage;
import ru.spbstu.ChatService.repository.InvitationRepository;
import ru.spbstu.ChatService.repository.SessionRepository;

import java.util.UUID;

@Service
public class WebSocketService {

    @Value("${plintum.chatservice.url}")
    private String url;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean isRequiredAuth;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SendMailJavaAPI mailJavaAPI;

    public void sendInviteByEmail(String roomId, ChatMessage message) {
        Session session = sessionRepository.getBySessionUID(roomId);
        Invitation invitation = new Invitation(UUID.randomUUID().toString(), message.getSendTo(), session);
        invitationRepository.save(invitation);

        String emailMessage = String.format(
                "Hello! \n" +
                        "You were invited to the chat-room '%s' by the operator '%s'. " +
                        "To accept the invitation, please, visit next link: " +
                        "%s/invite/%s",
                message.getContent(), message.getSender(), url, invitation.getInvitationUID()
        );

        if (isRequiredAuth) {
            mailSender.sendMail(message.getSendTo(), "[Plintum Chatboard] New invitation", emailMessage);
        }
        else {
            mailJavaAPI.sendMail(message.getSendTo(), "[Plintum Chatboard] New invitation", emailMessage);
        }
    }
}
