package com.example.annonces.RestController;

import com.example.annonces.domain.Ad;
import com.example.annonces.domain.User;
import com.example.annonces.dto.PageResponse;
import com.example.annonces.repo.AdRepository;
import com.example.annonces.repo.UserRepository;
import com.example.annonces.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepo;
    private final AdRepository adRepo;

    @Value("${app.upload.dir}")
    private String uploadRoot;

    public ProfileRestController(
            MongoTemplate mongoTemplate,
            UserRepository userRepo,
            AdRepository adRepo) {
        this.mongoTemplate = mongoTemplate;
        this.userRepo = userRepo;
        this.adRepo = adRepo;
    }

    /* ===================== USER ===================== */

    @GetMapping("/me")
    public User getProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepo.findByEmail(email).orElseThrow();
    }

    @PutMapping("/me")
    public User updateProfile(@RequestBody User updated) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepo.findByEmail(email).orElseThrow();

        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setPhone(updated.getPhone());

        return userRepo.save(user);
    }

    /* ===================== PHOTO ===================== */

    @PostMapping("/me/photo")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier vide");
        }

        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepo.findByEmail(email).orElseThrow();

        Path profileDir = Paths.get(uploadRoot, "profile-photos");
        Files.createDirectories(profileDir);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = profileDir.resolve(filename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setPhotoUrl("/profiles/" + filename);
        userRepo.save(user);

        return ResponseEntity.ok(user);
    }

    /* ===================== USER ADS ===================== */

    @GetMapping("/me/ads")
    public PageResponse<Ad> myAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        String email = SecurityUtils.getCurrentUserEmail();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Query query = new Query().with(pageable);
        query.addCriteria(Criteria.where("ownerId").is(email));

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
                dateCriteria = dateCriteria.gte(
                        fromDate.atStartOfDay().toInstant(ZoneOffset.UTC)
                );
            }
            if (toDate != null) {
                dateCriteria = dateCriteria.lte(
                        toDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
                );
            }
            query.addCriteria(dateCriteria);
        }

        List<Ad> results = mongoTemplate.find(query, Ad.class);
        long total = mongoTemplate.count(
                Query.of(query).limit(-1).skip(-1),
                Ad.class
        );

        Page<Ad> adsPage = new PageImpl<>(results, pageable, total);

        return PageResponse.of(adsPage);
    }
}
