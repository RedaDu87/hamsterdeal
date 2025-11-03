package com.example.annonces.config;

import com.example.annonces.domain.User;
import com.example.annonces.repo.UserRepository;
import com.example.annonces.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserRepository users;

    public JwtAuthFilter(JwtService jwt, UserRepository users) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        // 🟢 Étape 1 : ignorer les routes publiques
        if (isPublicPath(path)) {
            chain.doFilter(req, res);
            return;
        }

        String token = null;

        // 🔹 2) Cherche dans le cookie
        if (req.getCookies() != null) {
            token = Arrays.stream(req.getCookies())
                    .filter(c -> "AUTH".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        // 🔹 3) Sinon, cherche dans le header Authorization
        if (token == null) {
            String header = req.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }
        }

        // 🔒 4) Validation du token
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwt.parse(token).getBody();

                // Vérifie expiration
                if (claims.getExpiration().before(new Date())) {
                    clearAuthCookie(res);
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Charge l'utilisateur
                String email = claims.getSubject();
                User u = users.findByEmail(email).orElse(null);
                if (u != null) {
                    Set<SimpleGrantedAuthority> auth = u.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                            .collect(Collectors.toSet());

                    var authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (ExpiredJwtException e) {
                clearAuthCookie(res);
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                clearAuthCookie(res);
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    // 🧭 Méthode utilitaire pour définir les endpoints publics
    private boolean isPublicPath(String path) {
        return path.startsWith("/ad/")
                || path.equals("/")
                || path.startsWith("/ads")
                || path.startsWith("/api/auth")
                || path.startsWith("/api/geo")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/uploads")
                || path.startsWith("/profiles")
                || path.equals("/favicon.ico");
    }

    private void clearAuthCookie(HttpServletResponse res) {
        Cookie expired = new Cookie("AUTH", "");
        expired.setPath("/");
        expired.setMaxAge(0);
        expired.setHttpOnly(true);
        expired.setSecure(false); // true si HTTPS
        res.addCookie(expired);
    }
}
