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

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    // @Autowired
    // private MailSender mailSender;

    @Autowired
    private SendMailJavaAPI mailSender;

    public void sendInviteByEmail(String roomId, ChatMessage message) {
        Session session = sessionRepository.getBySessionUID(roomId);
        Invitation invitation = new Invitation(UUID.randomUUID().toString(), message.getSendTo(), session);
        invitationRepository.save(invitation);

        String emailMessage = String.format(
                "Hello! \n" +
                        "You were invited to the chat-room '%s' by the operator '%s'. " +
                        "To accept the invitation, please, visit next link: " +
                        "http://%s/invite/%s",
                message.getContent(), message.getSender(), url, invitation.getInvitationUID()
        );

        mailSender.sendMail(message.getSendTo(), "[Spring Web Chat] New invitation", emailMessage);
    }
}
