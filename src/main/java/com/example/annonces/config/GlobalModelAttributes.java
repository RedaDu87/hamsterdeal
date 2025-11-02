package com.example.annonces.config;

import com.example.annonces.domain.User;
import com.example.annonces.repo.UserRepository;
import com.example.annonces.util.SecurityUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository userRepo;

    public GlobalModelAttributes(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        return SecurityUtils.getCurrentUserEmail() != null;
    }

    @ModelAttribute("user")
    public User currentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email != null) {
            Optional<User> userOpt = userRepo.findByEmail(email);
            return userOpt.orElse(null);
        }
        return null;
    }
}
