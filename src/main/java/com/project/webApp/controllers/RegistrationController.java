package com.project.webApp.controllers;

import com.project.webApp.models.RegistrationRequest;
import com.project.webApp.services.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@Controller
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
//    private final Validator validator;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("request", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("request") @Valid RegistrationRequest request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        System.out.println("User name: " + request.getUserName());
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: " + request.getPassword());
        System.out.println("-------------------------------");

        model.addAttribute("showEmailMessage", true);

        registrationService.register(request);
        return "register";
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) {
        registrationService.confirmToken(token);
        return "redirect:/login";
    }

    @PostMapping("/customLogout")
    public String logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            request.getSession().invalidate();
        }
        return "redirect:/users";
    }

}