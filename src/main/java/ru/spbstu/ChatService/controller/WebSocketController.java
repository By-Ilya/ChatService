package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import ru.spbstu.ChatService.domain.Session;
import ru.spbstu.ChatService.domain.Invitation;
import ru.spbstu.ChatService.domain.Message;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.model.ChatMessage;
import ru.spbstu.ChatService.repository.SessionRepository;
import ru.spbstu.ChatService.repository.InvitationRepository;
import ru.spbstu.ChatService.repository.MessageRepository;
import ru.spbstu.ChatService.repository.UserRepository;
import ru.spbstu.ChatService.service.MailSender;

import java.util.Date;
import java.util.UUID;

import static java.lang.String.format;

@Controller
public class WebSocketController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private MailSender mailSender;

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage message) {
        if (message.getType() == ChatMessage.MessageType.INVITE_BY_EMAIL) {
            Session session = sessionRepository.getBySessionUID(roomId);
            Invitation invitation = new Invitation(UUID.randomUUID().toString(), message.getSendTo(), session);
            invitationRepository.save(invitation);

            String emailMessage = String.format(
                    "Hello! \n" +
                            "You were invited to the chat-room '%s' by the operator '%s'. " +
                            "To accept the invitation, please, visit next link: " +
                            "http://localhost:8080/invite/%s",
                    message.getContent(), message.getSender(), invitation.getInvitationUID()
            );

            mailSender.sendMail(message.getSendTo(), "[Spring Web Chat] New invitation", emailMessage);

            return;
        }

        if (message.getType() == ChatMessage.MessageType.CHAT) {
            Message newMessage = new Message();
            newMessage.setText(message.getContent());
            newMessage.setDateTime(new Date());

            message.setTime(new Date());

            User user = userRepository.getByLogin(message.getSender());
            newMessage.setAuthor(user);

            Session session = sessionRepository.getBySessionUID(roomId);
            newMessage.setSession(session);

            messageRepository.save(newMessage);
        }

        messagingTemplate.convertAndSend(format("/channel/%s", roomId), message);
    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessage message,
                        SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);

        Session session = sessionRepository.getBySessionUID(roomId);

        if (session == null) {
            sessionRepository.save(new Session(roomId, message.getContent()));
        }

        if (currentRoomId != null) {
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setType(ChatMessage.MessageType.CHAT);
            leaveMessage.setSender(message.getSender());
            messagingTemplate.convertAndSend(format("/channel/%s", roomId), leaveMessage);
        }

        headerAccessor.getSessionAttributes().put("username", message.getSender());
        messagingTemplate.convertAndSend(format("/channel/%s", roomId), message);
    }
}
