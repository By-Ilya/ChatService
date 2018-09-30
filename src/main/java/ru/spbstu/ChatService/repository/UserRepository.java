package ru.spbstu.ChatService.repository;


import org.springframework.data.repository.CrudRepository;
import ru.spbstu.ChatService.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User getByLogin(String name);
    User getByEmail(String email);
}
