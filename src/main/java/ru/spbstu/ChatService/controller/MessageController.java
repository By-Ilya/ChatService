package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.ChatService.domain.HistoryMessage;
import ru.spbstu.ChatService.domain.Dialog;
import ru.spbstu.ChatService.domain.Message;
import ru.spbstu.ChatService.domain.Session;
import ru.spbstu.ChatService.repository.DialogRepository;
import ru.spbstu.ChatService.repository.MessageRepository;
import ru.spbstu.ChatService.repository.SessionRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getAllDialogMessages(@PathVariable("uuid") String sessionUID) {
        Session session = sessionRepository.getBySessionUID(sessionUID);
        Dialog lastDialog = dialogRepository.getTopBySessionIdOrderByIdDesc(session.getId());
        List<Message> messages = messageRepository.getMessagesByDialog(lastDialog);

        List<HistoryMessage> historyMessages = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                historyMessages.add(new HistoryMessage(message.getText(),
                        message.getAuthor().getLogin(),
                        message.getDateTime()));
            }

            return ResponseEntity.ok(historyMessages);
        }

        return ResponseEntity.ok(null);
    }
}
