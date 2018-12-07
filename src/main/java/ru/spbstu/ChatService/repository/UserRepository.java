package ru.spbstu.ChatService.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.spbstu.ChatService.domain.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u.login FROM User u WHERE u.active = true and u.login <> 'admin'")
    List<User> getOnlineUsers();

    @Query("SELECT u FROM User u WHERE u.login <> 'admin'")
    List<User> getAllUsers();

    User getByLogin(String name);
    User getByEmail(String email);
    User getByActivationCode(String activationCode);
}
