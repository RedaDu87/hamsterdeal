package com.example.annonces.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // /uploads/** → fichiers dans /app/uploads
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");

        // Optionnel profil : seulement si tu utilises
        registry
                .addResourceHandler("/profiles/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/profile-photos/");
    }
}
