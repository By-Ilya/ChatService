package ru.spbstu.ChatService.repository;

import org.springframework.data.repository.CrudRepository;
import ru.spbstu.ChatService.domain.Dialog;
import ru.spbstu.ChatService.domain.Message;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> getMessagesByDialog(Dialog dialog);
}
