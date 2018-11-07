package ru.spbstu.ChatService.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.ChatService.repository.DialogRepository;
import ru.spbstu.ChatService.repository.UserRepository;

@RestController
@RequestMapping("/dialogs/")
public class DialogController {

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public void addDialog() {

    }

    @PostMapping("/add")
    public void addUser() {
        
    }
}
