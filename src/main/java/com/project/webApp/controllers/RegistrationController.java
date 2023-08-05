package com.project.webApp.controllers;

import com.project.webApp.models.RegistrationRequest;
import com.project.webApp.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

//import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

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
        System.out.println(request.getUserName());
        System.out.println(request.getEmail());
        System.out.println(request.getPassword());

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