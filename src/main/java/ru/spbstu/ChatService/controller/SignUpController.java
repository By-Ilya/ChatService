package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.ChatService.domain.Role;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;
import ru.spbstu.ChatService.service.MailSender;
import ru.spbstu.ChatService.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.UUID;


@Controller
public class SignUpController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailSender mailSender;

    @GetMapping("/signup")
    public String showSignOutPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String doSignUp(HttpServletRequest request, Model model, User user,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String password,
                           @RequestParam(defaultValue = "") String email) {

        username = username.trim();
        password = password.trim();
        email = email.trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute("message", "Please, enter all fields!");
            return "signup";
        }

        if (userRepository.getByLogin(username) != null) {
            model.addAttribute("message", "User '" + username + "' exists!");
            return "signup";
        }

        if (userRepository.getByEmail(email) != null) {
            model.addAttribute("message", "User with this e-mail already exists!");
            return "signup";
        }

        user.setLogin(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to Spring Web Chat! To activate your account, please, visit next link: " +
                        "http://localhost:8080/activate/%s",
                user.getLogin(), user.getActivationCode()
        );

        mailSender.sendMail(user.getEmail(), "[Spring Web Chat] Activation code", message);

        long userId = userRepository.save(user).getId();
        System.out.println("Add user id: " + userId);

        request.getSession().setAttribute("username", username);

        return "redirect:/";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        if (!isActivated) {
            model.addAttribute("message", "Activation code not found!");
            return "login";
        }

        model.addAttribute("message", "User successfully activated!");
        return "login";
    }


    @RequestMapping(path = "/goToSignInPage")
    public String goToSignInPage() {
        return "redirect:/login";
    }
}
