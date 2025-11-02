package com.example.annonces.view;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class AuthViewAdvice {

    private final AuthViewHelper helper;
    public AuthViewAdvice(AuthViewHelper helper){ this.helper = helper; }

    @ModelAttribute
    public void exposeAuth(Model model, HttpServletRequest req){
        var info = helper.resolve(req);
        model.addAttribute("isAuthenticated", info.authenticated());
        model.addAttribute("userEmail", info.email());
        model.addAttribute("userRoles", info.roles());
        // éventuellement: model.addAttribute("isAdmin", info.roles().contains("ADMIN"));
    }
}