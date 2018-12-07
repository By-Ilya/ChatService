package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.ChatService.domain.Role;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMINISTRATE')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showAdminPage(Model model) {
        model.addAttribute("users", userRepository.getAllUsers());

        return "admin";
    }

    @GetMapping("/changePassword")
    public String showChangePasswordPage() {
        return "adminPassword";
    }

    @PostMapping("/changePassword")
    public String changeAdminPassword(Model model,
                                      @RequestParam String prevPassword,
                                      @RequestParam String newPassword) {
        if (prevPassword.isEmpty() || newPassword.isEmpty()) {
            model.addAttribute("message", "Please, enter all fields!");

            return "adminPassword";
        }

        User administrator = userRepository.getByLogin("admin");
        if (!passwordEncoder.matches(prevPassword, administrator.getPassword())) {
            model.addAttribute("message", "Incorrect previous password!");

            return "adminPassword";
        }

        administrator.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(administrator);

        return "redirect:/admin?passwordChanged";
    }

    @GetMapping("{user}")
    public String userEditFrom(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "userEdit";
    }

    @PostMapping
    public String saveUserData(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam Map<String, String> form,
                               @RequestParam("userId") User user) {
        user.setLogin(username);
        user.setEmail(email);

        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepository.save(user);

        return "redirect:/admin?userChanged";
    }
}
