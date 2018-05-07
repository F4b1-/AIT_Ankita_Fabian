package com.unibz.hikinghelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Configuration
@RestController
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired PasswordEncoder passwordEncoder;

   /* public IndexController(InMemoryUserDetailsManager inMemoryUserDetailsManager) {
        this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
    } */

    @RequestMapping("exists/{username}")
    public boolean userExists(@PathVariable("username") String username ) {
        return inMemoryUserDetailsManager.userExists(username);
    }

    @RequestMapping("addUser")
    public String add(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean userExists = inMemoryUserDetailsManager.userExists(username);
        if(username != null && password != null && !userExists) {
            inMemoryUserDetailsManager.createUser(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .roles("USER")
                    .build());
            return "added";
        } else {
            return "Registration not possible";
        }


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