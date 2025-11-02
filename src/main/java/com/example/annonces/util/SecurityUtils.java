package com.example.annonces.util;

import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Cookie httpOnlyJwtCookie(String token){
        Cookie c = new Cookie("AUTH", token);
        c.setHttpOnly(true);
        c.setPath("/");
        c.setSecure(false); // mettre true derrière HTTPS
        c.setMaxAge(24*3600);
        c.setAttribute("SameSite", "Strict");
        return c;
    }
    public static Cookie clearJwtCookie(){
        Cookie c = new Cookie("AUTH", "");
        c.setMaxAge(0);
        c.setPath("/");
        return c;
    }

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName(); // le principal = email
        }
        return null;
    }
}