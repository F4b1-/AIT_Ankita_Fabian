package com.unibz.hikinghelper;

import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.services.HikingUserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @Autowired PasswordEncoder passwordEncoder;

    @Autowired HikingUserDetailsServiceImpl userDetailsService;

    @RequestMapping("exist/{username}")
    public boolean userExists(@PathVariable("username") String username ) {
        return userDetailsService.loadUserByUsername(username) != null;
    }

    @RequestMapping("addUser")
    public String add(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter(Constants.FORM_USERNAME);
        String password = request.getParameter(Constants.FORM_PASSWORD);
        if(username != null && password != null) {
            userDetailsService.saveUser(username, password, Constants.ROLE_USER);
            return "added";
        } else {
            return "Registration not possible";
        }


    }

    @RequestMapping("/cart")
    public String cart(HttpSession session) {
        session.setAttribute("hey", "jajajaj");
        return "schau mer mal";
    }

    @RequestMapping("/cart2")
    public String cart2(HttpSession session) {
        String s = (String) session.getAttribute("hey");
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