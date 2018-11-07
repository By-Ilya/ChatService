package ru.spbstu.ChatService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;

import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean activateUser(String activationCode) {
        User user = userRepository.getByActivationCode(activationCode);

        if (user == null) {
            return false;
        }

        user.setActivationCode("activated");
        userRepository.save(user);

        return true;
    }

    public String generateNickname() {
        String username;
        Random random = new Random();

        char[] word = new char[random.nextInt(8) + 5];
        word[0] = (char)('A' + random.nextInt(26));

        for (int i = 1; i < word.length; i++) {
            word[i] = (char)('a' + random.nextInt(26));
        }
        username = new String(word);

        return username;
    }
}
