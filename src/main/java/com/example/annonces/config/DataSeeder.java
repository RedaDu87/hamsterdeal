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

            System.out.println("🧹 Nettoyage MongoDB...");
            adRepo.deleteAll();
            userRepo.deleteAll();

            /* ============================================================
               🔐 1) Création ADMIN
               ============================================================ */

            User admin = new User();
            admin.setEmail("admin@example.com");

            // ⚠️ METTRE UN VRAI HASH BCRYPT !
            admin.setPasswordHash("$2a$10$ZpP8hV9G23/JIUNkGUfUTO/nZDaAg8jh1IGnh07GXOXy7JYaeYhCK"); // pour tests uniquement admin123

            admin.setRoles(Set.of(Role.ADMIN));
            admin.setCreatedAt(Instant.now());
            admin.setFirstName("Admin");
            admin.setLastName("SuperUser");
            admin.setPhone("+41 79 000 00 00");
            admin.setPhotoUrl("https://picsum.photos/seed/admin/200/200");

            admin = userRepo.save(admin);
            System.out.println("✅ Admin créé : " + admin.getId());


            /* ============================================================
               📝 2) Génération d’annonces appartenant à l’admin
               ============================================================ */

            List<String> categories = Arrays.asList("car", "electronics", "home", "fashion");
            List<String> cantons = Arrays.asList("VD", "GE", "FR", "VS", "ZH");
            List<String> conditions = Arrays.asList("new", "good", "used");

            List<Ad> ads = new ArrayList<>();

            String loremLong = """
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
    Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 
    Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
    Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
    """;

            for (String category : categories) {

                for (int i = 1; i <= 20; i++) {   // 👉 génère 20 annonces par catégorie

                    Ad ad = new Ad();
                    ad.setTitle(category + " annonce " + i);
                    ad.setDescription("Annonce " + i + " dans la catégorie " + category + " " +loremLong);
                    ad.setPrice(BigDecimal.valueOf(random.nextInt(2000) + 100));
                    ad.setCategory(category);
                    ad.setOwnerId(admin.getEmail());   // 🔥 Toutes les annonces appartiennent à l'ADMIN
                    ad.setCreatedAt(Instant.now());
                    ad.setActive(true);
                    ad.setCanton(cantons.get(random.nextInt(cantons.size())));
                    ad.setCondition(conditions.get(random.nextInt(conditions.size())));
                    ad.setCity("Lausanne");

                    /* ===== 📸 Génération de 20 photos ===== */

                    List<ImageRef> images = new ArrayList<>();
                    for (int j = 1; j <= 20; j++) {
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

            System.out.println("📸 Chaque annonce contient 20 photos.");
            System.out.println("✅ " + ads.size() + " annonces générées pour l’admin.");
        };
    }
}
