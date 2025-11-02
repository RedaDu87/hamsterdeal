package com.example.annonces.controller;

import com.example.annonces.domain.User;
import com.example.annonces.dto.LoginRequest;
import com.example.annonces.dto.RegisterRequest;
import com.example.annonces.repo.UserRepository;
import com.example.annonces.service.JwtService;
import com.example.annonces.service.UserService;
import com.example.annonces.util.SecurityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Controller
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepo;
    private final JwtService jwt;
    public AuthController(UserService u, UserRepository r, JwtService j){this.userService=u;this.userRepo=r;this.jwt=j;}

    @GetMapping("/login")
    public String loginPage(Model model){
        model.addAttribute("login", new LoginRequest());
        return "login";
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<Void> doLogin(@Valid @ModelAttribute("login") LoginRequest req,
                                        BindingResult br,
                                        HttpServletResponse res) {
        if (br.hasErrors()) return ResponseEntity.badRequest().build();

        var u = userRepo.findByEmail(req.getEmail().toLowerCase()).orElse(null);
        if (u == null || !userService.checkPassword(u, req.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwt.generate(u.getEmail(), Map.of("roles", u.getRoles()));

        ResponseCookie cookie = ResponseCookie.from("AUTH", token)
                .httpOnly(true)
                .secure(false)       // passe à true derrière HTTPS
                .path("/")
                .sameSite("Lax")     // "Strict" si tu veux, "None" si cross-site (avec secure)
                .maxAge(Duration.ofDays(1))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("register", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("register") RegisterRequest req, BindingResult br, Model model){
        if(br.hasErrors()) return "register";
        try{
            userService.register(req);
            model.addAttribute("message", "Compte créé. Connectez-vous.");
            return "login";
        }catch(IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse res) {
        ResponseCookie expired = ResponseCookie.from("AUTH", "sc")
                .httpOnly(true)         // pareil que login
                .secure(false)          // pareil que login (true si HTTPS en prod)
                .path("/")              // pareil
                .sameSite("Lax")        // pareil
                .maxAge(0)              // expire immédiatement
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, expired.toString());

        return "redirect:/"; // recharge la page pour vider l'affichage
    }





}