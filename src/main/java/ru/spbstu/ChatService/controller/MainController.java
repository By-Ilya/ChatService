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
    public String showLoginPage() {
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, @RequestParam(defaultValue = "") String username,
                          @RequestParam(defaultValue = "") String password) {
        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return "login";
        }

        User dataSet = userRepository.getByLogin(username);

        if (dataSet == null || !dataSet.getPassword().equals(password)) {
            return "login";
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

    @RequestMapping(path = "/signout", method = RequestMethod.GET)
    public String showSignOutPage() {
        return "signout";
    }


    @RequestMapping(path = "/goToSignInPage")
    public String goToSignInPage(HttpServletRequest request) {
        return "redirect:/login";
    }


    @RequestMapping(path = "/login-error", method = RequestMethod.GET)
    public String showLoginPageWithError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @RequestMapping(path = "/login-error", method = RequestMethod.POST)
    public String doLoginAfterError(HttpServletRequest request, @RequestParam(defaultValue = "") String username,
                                    @RequestParam(defaultValue = "") String password, Model model) {
        return doLogin(request, username, password);
    }


    @RequestMapping(path = "/signup", method = RequestMethod.GET)
    public String showSignUpPage() {
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
            return "redirect:/signup-error";
        }


        if (userRepository.getByLogin(username) != null) {
            return "redirect:/signup-error";
        }

        long userId = userRepository.save(new User(username, email, password, false)).getId();
        System.out.println("Add user id: " + userId);


        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("password", password);

        return "redirect:/";
    }

    @RequestMapping(path = "/signup-error", method = RequestMethod.GET)
    public String showSignUpPageWithError(Model model) {
        model.addAttribute("signUpError", true);
        return "/signup";
    }

    @RequestMapping(path = "/signup-error", method = RequestMethod.POST)
    public String doSignUpAfterError(HttpServletRequest request, @RequestParam(defaultValue = "") String username,
                                     @RequestParam(defaultValue = "") String email,
                                     @RequestParam(defaultValue = "") String password) {
        return doSignUp(request, username, email, password);
    }

}
