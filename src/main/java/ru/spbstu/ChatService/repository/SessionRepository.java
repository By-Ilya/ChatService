package ru.spbstu.ChatService.repository;

import org.springframework.data.repository.CrudRepository;
import ru.spbstu.ChatService.domain.Session;

public interface SessionRepository extends CrudRepository<Session, Long> {

    Session getBySessionUID(String sessionUID);
}
