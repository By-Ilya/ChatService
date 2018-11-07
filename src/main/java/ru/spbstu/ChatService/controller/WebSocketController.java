package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import ru.spbstu.ChatService.domain.Message;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.model.ChatMessage;
import ru.spbstu.ChatService.repository.MessageRepository;
import ru.spbstu.ChatService.repository.UserRepository;

import java.util.Date;

import static java.lang.String.format;

@Controller
public class WebSocketController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage message) {
        if (message.getType() == ChatMessage.MessageType.CHAT) {
            Message newMessage = new Message();
            newMessage.setText(message.getContent());
            newMessage.setDateTime(new Date());

            message.setTime(new Date());

            User user = userRepository.getByLogin(message.getSender());
            newMessage.setAuthor(user);

            messageRepository.save(newMessage);
        }

        messagingTemplate.convertAndSend(format("/channel/%s", roomId), message);
    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessage message,
                        SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);

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
