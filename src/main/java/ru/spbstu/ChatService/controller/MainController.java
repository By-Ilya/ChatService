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
import ru.spbstu.ChatService.domain.Session;
import ru.spbstu.ChatService.domain.Invitation;
import ru.spbstu.ChatService.domain.Role;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.InvitationRepository;
import ru.spbstu.ChatService.repository.UserRepository;
import ru.spbstu.ChatService.service.UserService;

import java.util.Collection;
import java.util.Collections;


@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        model.addAttribute("username", authentication.getName());


        if (authentication.getAuthorities().toArray()[0].equals(Role.OPERATOR)) {
            model.addAttribute("role", "operator");
            return "chat";
        }

        model.addAttribute("role", "user");
        return "chat";
    }

    @GetMapping("/logout")
    public String logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.getByLogin(authentication.getName());
        user.setActive(false);
        userRepository.save(user);

        return "login";
    }

    @GetMapping("/invite/{uuid}")
    public String inviteAnonymousUser(Model model, @PathVariable String uuid) {
        Invitation invitation = invitationRepository.getInvitationByInvitationUID(uuid);

        if (invitation == null) {
            model.addAttribute("message", "Invitation doesn't valid!");
            return "login";
        }

        Session session = invitation.getSession();
        if (session == null) {
            model.addAttribute("message", "This chat-room not found!");
            return "login";
        }

        User user = userRepository.getByEmail(invitation.getEmail());
        if (user == null) {
            user = new User();
            user.setLogin(userService.generateNickname());
            user.setEmail(invitation.getEmail());
            user.setActivationCode("activated");
            user.setActive(false);

            userRepository.save(user);
        }

        invitation.setInvitationUID("not valid");
        invitationRepository.save(invitation);

        model.addAttribute("username", user.getLogin());
        model.addAttribute("role", "anonymous");
        model.addAttribute("roomId", session.getSessionUID());
        model.addAttribute("roomName", session.getName());

        return "chat";
    }
}
