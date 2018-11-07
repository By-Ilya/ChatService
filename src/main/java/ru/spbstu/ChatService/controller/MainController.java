package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spbstu.ChatService.domain.Role;
import ru.spbstu.ChatService.service.UserService;


@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        model.addAttribute("username", authentication.getName());

        if (authentication.getAuthorities().toArray()[0].toString().equals(Role.OPERATOR.toString())) {
            model.addAttribute("role", "operator");
            return "chat";
        }

        model.addAttribute("role", "user");
        return "chat";
    }

    @GetMapping("/invite/{roomId}")
    public String inviteAnonymousUser(Model model, @PathVariable String roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        authentication.setAuthenticated(true);

        model.addAttribute("username", userService.generateNickname());
        model.addAttribute("role", "anonymous");
        model.addAttribute("roomId", roomId);

        return "chat";
    }
}
