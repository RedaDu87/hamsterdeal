package com.example.annonces.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Dossier général des uploads (annonces, etc.)
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        if (!uploadPath.endsWith("/"))
            uploadPath += "/";

        // Dossier spécifique aux photos de profil
        String profilePath = Paths.get(uploadPath, "profile-photos").toAbsolutePath().normalize().toString();
        if (!profilePath.endsWith("/"))
            profilePath += "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);

        registry.addResourceHandler("/profiles/**")
                .addResourceLocations("file:" + profilePath);
    }
}
