package com.example.annonces.RestController;

import com.example.annonces.domain.User;
import com.example.annonces.dto.LoginRequest;
import com.example.annonces.dto.RegisterRequest;
import com.example.annonces.repo.UserRepository;
import com.example.annonces.service.JwtService;
import com.example.annonces.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth2")
public class AuthRestController {

    private final UserService userService;
    private final UserRepository userRepo;
    private final JwtService jwt;

    public AuthRestController(UserService userService,
                              UserRepository userRepo,
                              JwtService jwt) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.jwt = jwt;
    }

    // 🔑 LOGIN (Flutter)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {

        User u = userRepo.findByEmail(req.getEmail().toLowerCase())
                .orElse(null);

        if (u == null || !userService.checkPassword(u, req.getPassword())) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Email ou mot de passe invalide")
            );
        }

        // Role -> String (important pour JWT / Flutter)
        Set<String> roles = u.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        String token = jwt.generate(
                u.getEmail(),
                Map.of("roles", roles)
        );

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "email", u.getEmail(),
                        "roles", roles
                )
        );
    }

    // 📝 REGISTER (Flutter)
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            userService.register(req);
            return ResponseEntity.ok(
                    Map.of("message", "Compte créé avec succès")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // 👤 WHO AM I (token validation)
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        if (!jwt.isValid(token)) {
            return ResponseEntity.status(401).build();
        }

        String email = jwt.getSubject(token);

        User u = userRepo.findByEmail(email)
                .orElseThrow(); // token valide mais user supprimé = cas rare

        Set<String> roles = u.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(
                Map.of(
                        "email", email,
                        "roles", roles
                )
        );
    }
}
