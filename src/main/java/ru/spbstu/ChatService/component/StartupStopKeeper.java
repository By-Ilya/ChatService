package ru.spbstu.ChatService.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.spbstu.ChatService.domain.Role;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;

import java.util.Collections;
import java.util.List;


@Component
public class StartupStopKeeper {

    @Autowired
    private UserRepository userRepository;

    @Value("${plintum.chatservice.admin.login}")
    private String adminLogin;

    @Value("${plintum.chatservice.admin.password}")
    private String adminPassword;

    @Value("${spring.mail.username}")
    private String email;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void setAdminAccount() {
        if (userRepository.getByLogin("admin") == null) {
            User administrator = new User();

            PasswordEncoder passwordEncoder = getPasswordEncoder();

            administrator.setLogin(adminLogin);
            administrator.setEmail(email);
            administrator.setPassword(passwordEncoder.encode(adminPassword));
            administrator.setRoles(Collections.singleton(Role.ADMINISTRATE));
            administrator.setActivationCode("activated");
            administrator.setActive(false);

            userRepository.save(administrator);
        }

        List<User> userList = userRepository.getAllUsers();

        for (User user : userList) {
            user.setActive(false);
            userRepository.save(user);
        }
    }
}
