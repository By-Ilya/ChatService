package ru.spbstu.ChatService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spbstu.ChatService.domain.User;
import ru.spbstu.ChatService.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("username");
        String password = (String) request.getSession().getAttribute("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/login";
        }
        
        User dataSet = userRepository.getByLogin(username);

        if (dataSet == null || !dataSet.getPassword().equals(password)) {
            return "redirect:/login";
        }


        model.addAttribute("username", username);
        model.addAttribute("password", password);

        return "chat";
    }


    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String showLoginPage(HttpServletRequest request, Model model) {
        String message = (String) request.getSession().getAttribute("message");

        if (message != null) {
            model.addAttribute("message", message);
        }

        request.getSession().removeAttribute("message");

        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, @RequestParam(defaultValue = "") String username,
                          @RequestParam(defaultValue = "") String password) {
        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            request.getSession().setAttribute("message", "Please, enter all fields!");
            return "redirect:/login";
        }

        User dataSet = userRepository.getByLogin(username);

        if (dataSet == null || !dataSet.getPassword().equals(password)) {
            request.getSession().setAttribute("message", "Wrong username or password!");
            return "redirect:/login";
        }


        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("password", password);

        return "redirect:/";
    }

    @RequestMapping(path = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession(true).invalidate();

        return "redirect:/login";
    }

    @RequestMapping(path = "/signup", method = RequestMethod.GET)
    public String showSignOutPage(HttpServletRequest request, Model model) {
        String message = (String) request.getSession().getAttribute("message");

        if (message != null) {
            model.addAttribute("message", message);
        }

        request.getSession().removeAttribute("message");

        return "signup";
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST)
    public String doSignUp(HttpServletRequest request, @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String email,
                           @RequestParam(defaultValue = "") String password) {
        username = username.trim();
        email = email.trim();
        password = password.trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            request.getSession().setAttribute("message", "Please, enter all fields");
            return "redirect:/signup";
        }


        if (userRepository.getByLogin(username) != null) {
            request.getSession().setAttribute("message", "User '" + username + "' already exists!");
            return "redirect:/signup";
        }

        long userId = userRepository.save(new User(username, email, password, false)).getId();
        System.out.println("Add user id: " + userId);


        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("password", password);

        return "redirect:/";
    }

    @RequestMapping(path = "/goToSignInPage")
    public String goToSignInPage() {
        return "redirect:/login";
    }
}
