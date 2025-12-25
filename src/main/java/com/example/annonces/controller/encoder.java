package com.example.annonces.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class encoder {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();


    public static void main(String[] args) {
       System.out.println(encoder.encode("admin123"));
    }
}
