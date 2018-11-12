package ru.spbstu.ChatService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;

import java.util.Random;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByLogin(username);

        if (!user.getActivationCode().equals("activated")) {
            return null;
        }

        user.setActive(true);
        userRepository.save(user);

        return user;
    }


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

        char[] vowels = {'a', 'e', 'i', 'o', 'y'};
        char[] consonants = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
                'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};

        Random random = new Random();

        char[] word = new char[random.nextInt(8) + 5];

        for (int i = 0; i < word.length; i++) {
            if (i % 2 == 0) {
                word[i] = consonants[random.nextInt(consonants.length)];
            }
            else {
                word[i] = vowels[random.nextInt(vowels.length)];
            }
        }

        word[0] = Character.toUpperCase(word[0]);
        username = new String(word) + random.nextInt(999);

        return username;
    }
}
