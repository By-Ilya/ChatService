package ru.spbstu.ChatService.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.ChatService.repository.SessionRepository;
import ru.spbstu.ChatService.repository.UserRepository;


@RestController
@RequestMapping("/sessions/")
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/addSession")
    public void addSession() {

    }
}
