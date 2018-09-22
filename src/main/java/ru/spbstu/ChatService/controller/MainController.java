package ru.spbstu.ChatService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spbstu.ChatService.dbService.dataSets.UserDataSet;
import ru.spbstu.ChatService.dbService.executor.DBException;
import ru.spbstu.ChatService.dbService.executor.DBServiceImpl;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    private final DBServiceImpl dbService = new DBServiceImpl();

    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("username");
        String password = (String) request.getSession().getAttribute("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/login";
        }

        try {
            UserDataSet dataSet = dbService.getUserByLogin(username);

            if (dataSet == null || !dataSet.getPassword().equals(password)) {
                return "redirect:/login";
            }
        }

        catch (DBException err) {
            err.printStackTrace();
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

        try {
            UserDataSet dataSet = dbService.getUserByLogin(username);

            if (dataSet == null || !dataSet.getPassword().equals(password)) {
                return "login";
            }
        }

        catch (DBException err) {
            err.printStackTrace();
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

    @RequestMapping(path = "/signout", method = RequestMethod.POST)
    public String doSignOut(HttpServletRequest request, @RequestParam(defaultValue = "") String username,
                            @RequestParam(defaultValue = "") String email,
                            @RequestParam(defaultValue = "") String password) {
        username = username.trim();
        email = email.trim();
        password = password.trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return "signout";
        }

        try {
            if (dbService.getUserByLogin(username) != null) {
                return "signout";
            }

            long userId = dbService.addUser(username, email, password);
            System.out.println("Add user id: " + userId);
        }

        catch (DBException err) {
            err.printStackTrace();
        }

        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("password", password);

        return "redirect:/";
    }

    @RequestMapping(path = "/goToSignInPage")
    public String goToSignInPage(HttpServletRequest request) {
        return "redirect:/login";
    }
}
