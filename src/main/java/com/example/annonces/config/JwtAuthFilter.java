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
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
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

        String token = null;

        if (req.getCookies() != null) {
            token = Arrays.stream(req.getCookies())
                    .filter(c -> "AUTH".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (token == null) {
            String header = req.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwt.parse(token).getBody();

                if (claims.getExpiration().before(new Date())) {
                    clearAuthCookie(res); // <-- Cookie expiré → on l'efface
                } else {
                    String email = claims.getSubject();
                    User u = users.findByEmail(email).orElse(null);

                    if (u != null) {
                        Set<SimpleGrantedAuthority> auth = u.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                                .collect(Collectors.toSet());

                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(email, null, auth));
                    }
                }

            } catch (Exception e) {
                clearAuthCookie(res); // <-- On efface le cookie invalide
            }
        }

        chain.doFilter(req, res); // ✅ On laisse toujours passer la requête
    }

    private void clearAuthCookie(HttpServletResponse res) {
        Cookie expired = new Cookie("AUTH", "");
        expired.setPath("/");
        expired.setMaxAge(0);
        expired.setHttpOnly(true);
        expired.setSecure(false);
        res.addCookie(expired);
    }

}
