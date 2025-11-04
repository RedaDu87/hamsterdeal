package com.example.annonces.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;

        public SecurityConfig(JwtAuthFilter f) {
                this.jwtAuthFilter = f;
        }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .logout(AbstractHttpConfigurer::disable)
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // --- Fichiers statiques et uploads accessibles publiquement ---
                                                .requestMatchers("/api/geo/**").permitAll()
                                                .requestMatchers(
                                                                "/", "/ads/**", "/ad/**", "/login", "/register",
                                                                "/css/**", "/js/**", "/uploads/**", "/profiles/**",
                                                                "/favicon.ico", "/api/auth/**")
                                                .permitAll()

                                                // --- Pages nécessitant une authentification ---
                                                .requestMatchers(
                                                                "/ad/new", "/ad/save", "/ad/edit/**", "/ad/update/**",
                                                                "/ad/delete/**", "/ad/my", "/profile",
                                                                "/api/ads/search")
                                                .authenticated()

                                                // --- Tout le reste ---
                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
                return cfg.getAuthenticationManager();
        }
}
