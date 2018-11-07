package ru.spbstu.ChatService.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.spbstu.ChatService.domain.Message;

import java.util.Date;

@Getter
@Setter
@Data
public class ChatMessage {
    public enum MessageType {
        CHAT,
        JOIN,
        INVITE,
        LEAVE
    }

    private MessageType type;
    private String content;
    private String sender;
    private Date time;

    public ChatMessage(Message message) {
        this.content = message.getText();
        this.sender = message.getAuthor().getLogin();
        this.time = message.getDateTime();
        this.type = MessageType.CHAT;
    }

    public ChatMessage() { }
}
