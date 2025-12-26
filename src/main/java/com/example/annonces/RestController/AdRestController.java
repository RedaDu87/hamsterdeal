package com.example.annonces.RestController;

import com.example.annonces.domain.Ad;
import com.example.annonces.domain.ImageRef;
import com.example.annonces.dto.AdRequest;
import com.example.annonces.dto.PageResponse;
import com.example.annonces.mapper.AdMapper;
import com.example.annonces.repo.AdRepository;
import com.example.annonces.repo.UserRepository;
import com.example.annonces.service.AdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdRestController {

    private final AdService adService;
    private final AdRepository adRepo;
    private final UserRepository userRepo;
    private final Path uploadRoot;

    public AdRestController(
            AdService adService,
            AdRepository adRepo,
            UserRepository userRepo,
            @Value("${app.upload.dir}") String uploadDir) throws IOException {

        this.adService = adService;
        this.adRepo = adRepo;
        this.userRepo = userRepo;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadRoot);
    }

    // 🔹 Liste paginée + filtres
    @GetMapping
    public ResponseEntity<PageResponse<Ad>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String canton,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        String sortDir = sortParts.length > 1 ? sortParts[1] : "desc";

        Page<Ad> result = adService.list(
                page, size, q, category, canton,
                priceMin, priceMax, sortField, sortDir
        );

        return ResponseEntity.ok(PageResponse.of(result));
    }

    // 🔹 Détail annonce
    @GetMapping("/{id}")
    public ResponseEntity<Ad> detail(@PathVariable String id) {
        return adRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Création annonce
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ad> create(
            @Valid @RequestPart("ad") AdRequest req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Principal principal) throws IOException {

        Ad ad = AdMapper.toEntity(req, principal.getName());
        ad.setImages(handleFiles(files, req.getTitle()));
        adRepo.save(ad);

        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

    // 🔹 Mise à jour annonce
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ad> update(
            @PathVariable String id,
            @Valid @RequestBody AdRequest req,
            Principal principal) {

        Ad existing = adRepo.findById(id).orElseThrow();

        if (!existing.getOwnerId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Ad updated = AdMapper.toEntity(req, principal.getName());
        updated.setId(existing.getId());
        updated.setImages(existing.getImages());

        adRepo.save(updated);
        return ResponseEntity.ok(updated);
    }


    // 🔹 Suppression image
    @DeleteMapping("/{adId}/images/{index}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String adId,
            @PathVariable int index,
            Principal principal) {

        Ad ad = adRepo.findById(adId).orElseThrow();

        if (!ad.getOwnerId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (ad.getImages() != null && index >= 0 && index < ad.getImages().size()) {
            ad.getImages().remove(index);
            adRepo.save(ad);
        }

        return ResponseEntity.noContent().build();
    }

    // 🔹 Suppression annonce
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {

        Ad ad = adRepo.findById(id).orElseThrow();

        if (!ad.getOwnerId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        adRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 🔧 Utilitaire upload
    private List<ImageRef> handleFiles(List<MultipartFile> files, String alt) throws IOException {
        List<ImageRef> list = new ArrayList<>();
        if (files == null) return list;

        for (MultipartFile f : files) {
            if (f.isEmpty()) continue;

            String original = Paths.get(f.getOriginalFilename()).getFileName().toString();
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String name = java.util.UUID.randomUUID() + ext;

            Path dest = uploadRoot.resolve(name);
            Files.copy(f.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            ImageRef ref = new ImageRef();
            ref.setUrl("/uploads/" + name);
            ref.setAlt(alt);
            list.add(ref);
        }
        return list;
    }
}
