package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;
import ru.spbstu.ChatService.service.MailSender;
import ru.spbstu.ChatService.service.SendMailJavaAPI;

@Controller
@RequestMapping("/resend")
public class ResendController {

    @Value("${plintum.chatservice.url}")
    private String url;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean isRequiredAuth;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SendMailJavaAPI mailJavaAPI;

    @GetMapping
    public String showResendPage() {
        return "resend";
    }

    @PostMapping
    public String sendEmailMessage(Model model,
                                   @RequestParam String email) {
        User user = userRepository.getByEmail(email);

        if (user == null) {
            model.addAttribute("message", "User with this e-mail not found!");

            return "signup";
        }

        if (user.getActivationCode().equals("activated")) {
            model.addAttribute("message", "User with this e-mail already activated");

            return "login";
        }

        String message = String.format(
                "Hello, %s! \nWelcome to Spring Web Chat! " +
                        "This is the repeat message to activate your account." +
                        "To activate your account, please, visit next link: %s/activate/%s",
                user.getLogin(), url, user.getActivationCode()
        );

        if (isRequiredAuth) {
            mailSender.sendMail(user.getEmail(), "[Spring Web Chat] Activation code", message);
        }
        else {
            mailJavaAPI.sendMail(user.getEmail(), "[Spring Web Chat] Activation code", message);
        }

        model.addAttribute("message", "Please, check your e-mail to activate your account!");

        return "login";
    }
}
