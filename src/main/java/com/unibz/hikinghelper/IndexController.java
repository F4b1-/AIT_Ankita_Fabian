package com.unibz.hikinghelper;

import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.model.User;
import com.unibz.hikinghelper.services.HikingUserDetailsServiceImpl;
import com.unibz.hikinghelper.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;

@Configuration
@RestController
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    HikingUserDetailsServiceImpl userDetailsService;

    @RequestMapping("exist/{username}")
    public boolean userExists(@PathVariable("username") String username) {
        return userDetailsService.loadUserByUsername(username) != null;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public RedirectView add(@ModelAttribute User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username != null && password != null && !Utils.userExists(userDetailsService, username)) {
            userDetailsService.saveUser(username, password, Constants.ROLE_USER);
            return new RedirectView("/login?successful");
        } else {
            return new RedirectView("/login");
        }
    }

    @RequestMapping("/cart")
    public String cart(HttpSession session) {
        session.setAttribute("hey", "jajajaj");
        return "schau mer mal";
    }

    @RequestMapping("/cart2")
    public String cart2(HttpSession session) {
        String s = (String) session.getAttribute("favorites");
        return s;
    }

    @RequestMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher view = request.getRequestDispatcher("/static/login.html");
        // Get authenticated user name from SecurityContext
        try {
            view.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/")
    public void startPage(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher view = request.getRequestDispatcher("/static/startPage.html");
        // Get authenticated user name from SecurityContext
        try {
            view.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/aboutUs")
    public void aboutUs(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher view = request.getRequestDispatcher("/static/aboutUs.html");
        // Get authenticated user name from SecurityContext
        try {
            view.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/registration")
    public void registration(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher view = request.getRequestDispatcher("/static/registration.html");
        // Get authenticated user name from SecurityContext
        try {
            view.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}