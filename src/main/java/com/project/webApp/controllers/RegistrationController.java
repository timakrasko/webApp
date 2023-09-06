package com.project.webApp.controllers;

import com.project.webApp.models.RegistrationValidator;
import com.project.webApp.services.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
//    private final Validator validator;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("validator", new RegistrationValidator());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("validator") @Valid RegistrationValidator validator, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        model.addAttribute("showEmailMessage", true);

        registrationService.register(validator);
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