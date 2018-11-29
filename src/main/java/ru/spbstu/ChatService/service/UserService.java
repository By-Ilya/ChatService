package ru.spbstu.ChatService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.spbstu.ChatService.domain.Invitation;
import ru.spbstu.ChatService.domain.Role;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Value("${plintum.chatservice.host}")
    private String host;

    @Value("${plintum.chatservice.port}")
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByLogin(username);

        if (user != null) {
            if (user.getActivationCode().equals("activated")) {
                user.setActive(true);
                userRepository.save(user);
            }
        }

        return user;
    }


    public long addNewUser(User user, String username, String email, String password) {
        user.setLogin(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        String message = String.format(
                "Hello, %s! \nWelcome to Spring Web Chat! " +
                        "To activate your account, please, visit next link: http://%s:%d/activate/%s",
                user.getLogin(), host, port, user.getActivationCode()
        );

        mailSender.sendMail(user.getEmail(), "[Spring Web Chat] Activation code", message);

        long userId = userRepository.save(user).getId();

        return userId;
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

    public User loadUserByInvitation(Invitation invitation) {
        User user = userRepository.getByEmail(invitation.getEmail());

        if (user == null) {
            user = new User();
            user.setLogin(generateNickname());
            user.setEmail(invitation.getEmail());
            user.setRoles(Collections.singleton(Role.ANONYMOUS));
            user.setActivationCode("activated");
            user.setActive(true);
        }

        return user;
    }

    private String generateNickname() {
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
