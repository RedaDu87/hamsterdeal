package com.example.annonces.service;

import com.example.annonces.domain.Role;
import com.example.annonces.domain.User;
import com.example.annonces.dto.RegisterRequest;
import com.example.annonces.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    public UserService(UserRepository users){this.users=users;}

    public User register(RegisterRequest r){
        users.findByEmail(r.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email déjà utilisé");
        });

        User u = new User();
        u.setEmail(r.getEmail().toLowerCase());
        u.setPasswordHash(encoder.encode(r.getPassword()));
        u.setRoles(Set.of(Role.USER));
        u.setFirstName(r.getFirstName());
        u.setLastName(r.getLastName());
        u.setPhone(r.getPhone());
        u.setPhotoUrl(r.getPhotoUrl());

        return users.save(u);
    }


    public boolean checkPassword(User u, String raw){
        return encoder.matches(raw, u.getPasswordHash());
    }



}