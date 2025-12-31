package com.example.annonces.controller;

import com.example.annonces.domain.Ad;
import com.example.annonces.domain.User;
import com.example.annonces.dto.PageResponse;
import com.example.annonces.repo.AdRepository;
import com.example.annonces.repo.UserRepository;
import com.example.annonces.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Controller
public class ProfileController {
    @Autowired
    private MongoTemplate mongoTemplate;

    private final UserRepository userRepo;

    private final AdRepository adRepo;

    @Value("${app.upload.dir}")
    private String uploadRoot;

    public ProfileController(UserRepository userRepo, AdRepository adRepo) {
        this.userRepo = userRepo;
        this.adRepo = adRepo;
    }

    @GetMapping("/profile")
    public String profilePage(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {

        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepo.findByEmail(email).orElseThrow();

        model.addAttribute("user", user);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Query query = new Query().with(pageable);
        query.addCriteria(Criteria.where("ownerId").is(user.getEmail()));

        if (title != null && !title.isBlank()) {
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }
        if (category != null && !category.isBlank()) {
            query.addCriteria(Criteria.where("category").regex(category, "i"));
        }
        if (minPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice));
        }
        if (maxPrice != null) {
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        }
        if (fromDate != null || toDate != null) {
            Criteria dateCriteria = Criteria.where("createdAt");
            if (fromDate != null) {
                dateCriteria = dateCriteria.gte(fromDate.atStartOfDay().toInstant(ZoneOffset.UTC));
            }
            if (toDate != null) {
                dateCriteria = dateCriteria.lte(toDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
            }
            query.addCriteria(dateCriteria);
        }

        List<Ad> results = mongoTemplate.find(query, Ad.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Ad.class);

        Page<Ad> adsPage = new PageImpl<>(results, pageable, total);

        model.addAttribute("ads", adsPage.getContent());
        model.addAttribute("page", PageResponse.of(adsPage));

        // ✅ garder les filtres remplis
        model.addAttribute("title", title);
        model.addAttribute("category", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "profile";
    }

    @GetMapping("/api/profile/ads")
    @ResponseBody
    public PageResponse<Ad> profileAdsApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {

        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepo.findByEmail(email).orElseThrow();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Query query = new Query().with(pageable);
        query.addCriteria(Criteria.where("ownerId").is(user.getEmail()));

        if (title != null && !title.isBlank())
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        if (category != null && !category.isBlank())
            query.addCriteria(Criteria.where("category").regex(category, "i"));
        if (minPrice != null)
            query.addCriteria(Criteria.where("price").gte(minPrice));
        if (maxPrice != null)
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        if (fromDate != null || toDate != null) {
            Criteria c = Criteria.where("createdAt");
            if (fromDate != null)
                c = c.gte(fromDate.atStartOfDay().toInstant(ZoneOffset.UTC));
            if (toDate != null)
                c = c.lte(toDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
            query.addCriteria(c);
        }

        List<Ad> results = mongoTemplate.find(query, Ad.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Ad.class);

        return PageResponse.of(new PageImpl<>(results, pageable, total));
    }


    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User updated,
            @RequestParam("photoFile") MultipartFile photoFile,
            RedirectAttributes redirectAttributes) {

        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepo.findByEmail(email).orElseThrow();

        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setPhone(updated.getPhone());

        if (!photoFile.isEmpty()) {
            try {
                // 📂 Dossier où stocker la photo : app.upload.dir/profile-photos
                Path profileDir = Paths.get(uploadRoot, "profile-photos");
                if (!Files.exists(profileDir)) {
                    Files.createDirectories(profileDir);
                }

                String filename = UUID.randomUUID() + "_" + photoFile.getOriginalFilename();
                Path filePath = profileDir.resolve(filename);

                Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                user.setPhotoUrl("/profiles/" + filename);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        userRepo.save(user);
        redirectAttributes.addFlashAttribute("message", "Profil mis à jour !");
        return "redirect:/profile";
    }

}
