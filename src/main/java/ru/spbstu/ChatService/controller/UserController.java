package ru.spbstu.ChatService.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.getRegisteredUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/online")
    public ResponseEntity<?> getOnlineUsers() {
        List<User> users = userRepository.getOnlineUsers();
        return ResponseEntity.ok(users);
    }
}
