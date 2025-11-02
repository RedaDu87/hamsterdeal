package com.example.annonces.config;

import com.example.annonces.domain.*;
import com.example.annonces.repo.AdRepository;
import com.example.annonces.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Configuration
public class DataSeeder {

    private final Random random = new Random();

    @Bean
    CommandLineRunner seedDatabase(AdRepository adRepo, UserRepository userRepo) {
        return args -> {

            // --- 🧹 Suppression préalable des anciennes données ---
            System.out.println("🧹 Nettoyage des collections MongoDB...");
            adRepo.deleteAll();
            userRepo.deleteAll();

            // --- 👤 Création des utilisateurs ---
            User reda = new User();
            reda.setEmail("reda.berkouch@outlook.com");
            reda.setPasswordHash("{noop}password123"); // Pour tests sans encodage
            reda.setRoles(Set.of(Role.ADMIN));
            reda.setFirstName("Réda");
            reda.setLastName("Berkouch");
            reda.setPhone("+41 767 150 083");
            reda.setPhotoUrl("https://i.pravatar.cc/150?img=5");

            User lea = new User();
            lea.setEmail("lea.martin@example.com");
            lea.setPasswordHash("{noop}password123");
            lea.setRoles(Set.of(Role.USER));
            lea.setFirstName("Léa");
            lea.setLastName("Martin");
            lea.setPhone("+41 79 123 45 67");
            lea.setPhotoUrl("https://i.pravatar.cc/150?img=32");

            userRepo.saveAll(List.of(reda, lea));
            System.out.println("✅ Utilisateurs ajoutés : Réda & Léa");

            // --- 📦 Génération d’annonces factices ---
            List<String> categories = Arrays.asList("car", "electronics", "home", "fashion");
            List<String> cantons = Arrays.asList("VD", "GE", "FR", "VS", "ZH");
            List<String> conditions = Arrays.asList("new", "good", "used");

            List<Ad> ads = new ArrayList<>();

            for (String category : categories) {
                for (int i = 1; i <= 500000; i++) { // ⚠️ réduit à 50 pour éviter surcharge
                    Ad ad = new Ad();
                    ad.setTitle(category + " annonce " + i);
                    ad.setDescription("Annonce " + i + " dans la catégorie " + category);
                    ad.setPrice(BigDecimal.valueOf(random.nextInt(2000) + 100));
                    ad.setCategory(category);
                    ad.setOwnerId("reda.berkouch@outlook.com");
                    ad.setCreatedAt(Instant.now());
                    ad.setActive(true);
                    ad.setCanton(cantons.get(random.nextInt(cantons.size())));
                    ad.setCondition(conditions.get(random.nextInt(conditions.size())));
                    ad.setCity("Lausanne");

                    List<ImageRef> images = new ArrayList<>();
                    for (int j = 1; j <= 3; j++) {
                        ImageRef img = new ImageRef();
                        img.setUrl("https://picsum.photos/seed/" + UUID.randomUUID() + "/800/600");
                        img.setAlt("Image " + j);
                        images.add(img);
                    }
                    ad.setImages(images);
                    ads.add(ad);
                }
            }

            adRepo.saveAll(ads);
            System.out.println("✅ " + ads.size() + " annonces ajoutées avec images.");
        };
    }
}
