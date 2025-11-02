package com.example.annonces.view;

import com.example.annonces.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AuthViewHelper {
    private final JwtService jwt;
    public AuthViewHelper(JwtService jwt){ this.jwt = jwt; }

    public record AuthInfo(boolean authenticated, String email, List<String> roles){}

    public AuthInfo resolve(HttpServletRequest req){
        String token = null;
        if (req.getCookies()!=null){
            for (Cookie c : req.getCookies()){
                if ("AUTH".equals(c.getName())) { token = c.getValue(); break; }
            }
        }
        if (token == null || token.isBlank()) return new AuthInfo(false, null, List.of());

        try {
            Claims claims = jwt.parse(token).getBody();
            String email = claims.getSubject();
            Object r = claims.get("roles");
            List<String> roles = switch (r){
                case List<?> l -> l.stream().map(Object::toString).toList();
                case Map<?,?> m -> m.values().stream().map(Object::toString).toList();
                case String s -> List.of(s);
                default -> List.of();
            };
            return new AuthInfo(true, email, roles);
        } catch (Exception e){
            return new AuthInfo(false, null, List.of());
        }
    }
}