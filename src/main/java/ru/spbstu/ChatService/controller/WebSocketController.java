package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import ru.spbstu.ChatService.domain.Dialog;
import ru.spbstu.ChatService.domain.Message;
import ru.spbstu.ChatService.domain.Session;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.model.ChatMessage;
import ru.spbstu.ChatService.repository.DialogRepository;
import ru.spbstu.ChatService.repository.MessageRepository;
import ru.spbstu.ChatService.repository.SessionRepository;
import ru.spbstu.ChatService.repository.UserRepository;
import ru.spbstu.ChatService.service.WebSocketService;

import java.util.Date;

import static java.lang.String.format;

@Controller
public class WebSocketController {

    @Value("${plintum.chatservice.host}")
    private String host;

    @Value("${plintum.chatservice.port}")
    private int port;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage message) {
        if (message.getType() == ChatMessage.MessageType.INVITE_BY_EMAIL) {
            webSocketService.sendInviteByEmail(roomId, message);
            return;
        }

        if (message.getType() == ChatMessage.MessageType.NEW_DIALOG) {
            Session session = sessionRepository.getBySessionUID(roomId);
            dialogRepository.save(new Dialog(session));
        }

        if (message.getType() == ChatMessage.MessageType.CHAT) {
            Message newMessage = new Message();
            newMessage.setText(message.getContent());
            newMessage.setDateTime(new Date());

            message.setTime(new Date());

            User user = userRepository.getByLogin(message.getSender());
            newMessage.setAuthor(user);

            Session session = sessionRepository.getBySessionUID(roomId);
            Dialog dialog = dialogRepository.getTopBySessionIdOrderByIdDesc(session.getId());

            newMessage.setDialog(dialog);

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
            session = new Session(roomId, message.getContent());
            sessionRepository.save(session);
            dialogRepository.save(new Dialog(session));
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
