package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.spbstu.ChatService.domain.Invitation;
import ru.spbstu.ChatService.domain.Role;
import ru.spbstu.ChatService.domain.Session;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.InvitationRepository;
import ru.spbstu.ChatService.repository.UserRepository;
import ru.spbstu.ChatService.service.UserService;


@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        if (authentication.getAuthorities().contains(Role.ADMINISTRATE)) {
            return "redirect:/admin";
        }

        model.addAttribute("username", authentication.getName());
        model.addAttribute("userRoles", authentication.getAuthorities().toArray());
        model.addAttribute("allRoles", Role.values());

        return "chat";
    }

    @GetMapping("/invite/{uuid}")
    public String inviteAnonymousUser(Model model, @PathVariable String uuid) {
        Invitation invitation = invitationRepository.getInvitationByInvitationUID(uuid);

        Session session = invitation.getSession();
        if (session == null) {
            model.addAttribute("message", "This chat-room not found!");
            return "login";
        }

        User user = userService.loadUserByInvitation(invitation);

        if (user.isActive()) {
            model.addAttribute("message", "Invitation doesn't valid!");
            return "login";
        }

        user.setActive(true);
        userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("username", user.getLogin());
        model.addAttribute("userRoles", authentication.getAuthorities().toArray());
        model.addAttribute("allRoles", Role.values());
        model.addAttribute("roomId", session.getSessionUID());
        model.addAttribute("roomName", session.getName());

        return "chat";
    }
}
