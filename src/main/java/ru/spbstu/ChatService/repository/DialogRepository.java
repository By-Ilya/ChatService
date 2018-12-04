package ru.spbstu.ChatService.repository;

import org.springframework.data.repository.CrudRepository;
import ru.spbstu.ChatService.domain.Dialog;


public interface DialogRepository extends CrudRepository<Dialog, Long> {

    Dialog getTopBySessionIdOrderByIdDesc(Long sessionId);
}
