package ru.spbstu.ChatService.repository;

import org.springframework.data.repository.CrudRepository;
import ru.spbstu.ChatService.domain.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

}
