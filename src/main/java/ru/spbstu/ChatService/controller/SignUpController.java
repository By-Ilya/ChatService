package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;
import ru.spbstu.ChatService.service.UserService;


@Controller
public class SignUpController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signup")
    public String showSignOutPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String doSignUp(Model model, User user,
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

        long id = userService.addNewUser(user, username, email, password);
        System.out.println("Add user id: " + id);

        model.addAttribute("message", "Please, visit your e-mail to activate account.");

        return "login";
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
