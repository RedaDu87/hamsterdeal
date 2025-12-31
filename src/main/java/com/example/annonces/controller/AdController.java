package com.example.annonces.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;

@Controller
public class AdController {
    private final AdService adService;
    private final AdRepository adRepo;
    private final UserRepository userRepo; // 🆕
    private final Path uploadRoot;

    public AdController(AdService adService, AdRepository adRepo, UserRepository userRepo,
            @Value("${app.upload.dir}") String uploadDir) throws IOException {
        this.adService = adService;
        this.adRepo = adRepo;
        this.userRepo = userRepo; // 🆕
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadRoot);
    }

    @GetMapping("/")
    public String landing(Model model) {
        Page<Ad> page = adService.list(0, 8, null, null, null, null, null);
        model.addAttribute("latest", page.getContent());
        return "index";
    }

    @GetMapping("/ads")
    public String list(@RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String canton,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false, defaultValue = "createdAt,desc") String sort,
            Model model) {

        // découpe "createdAt,desc" → sortField = createdAt, dir = desc
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        String sortDir = (sortParts.length > 1) ? sortParts[1] : "desc";

        Page<Ad> p = adService.list(page, size, q, category, canton, priceMin, priceMax, sortField, sortDir);

        model.addAttribute("result", PageResponse.of(p));
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("canton", canton);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("sort", sortField);
        model.addAttribute("dir", sortDir);

        return "ads";
    }

    @GetMapping("/ad/{id}")
    public String detail(@PathVariable String id, Model model) {
        Ad ad = adRepo.findById(id).orElse(null);
        if (ad == null) {
            model.addAttribute("error", "Annonce introuvable");
            return "ads";
        }
        model.addAttribute("ad", ad);
        List<ImageRef> images = ad.getImages();
        List<ImageRef> thumbnails = List.of(); // valeur par défaut

        if (images != null && images.size() > 1) {
            int end = Math.min(images.size(), 6); // max 6 images
            thumbnails = images.subList(1, end);
        }

        model.addAttribute("thumbnails", thumbnails);

        // 🔥 rechercher le vendeur par email (ownerId)
        if (ad.getOwnerId() != null) {
            userRepo.findByEmail(ad.getOwnerId())
                    .ifPresent(owner -> model.addAttribute("owner", owner));
        }
        return "ad-detail";
    }

    @GetMapping("/ad/new")
    @PreAuthorize("isAuthenticated()")
    public String createForm(Model model) {
        model.addAttribute("ad", new AdRequest());
        return "ad-form";
    }

    @PostMapping("/ad/save")
    @PreAuthorize("isAuthenticated()")
    public String save(@Valid @ModelAttribute("ad") AdRequest req,
            @RequestParam(name = "files", required = false) List<MultipartFile> files,
            Principal principal) throws IOException {

        Ad ad = AdMapper.toEntity(req, principal.getName());

        if (files != null && !files.isEmpty()) {
            var list = new java.util.ArrayList<ImageRef>();
            for (MultipartFile f : files) {
                if (f.isEmpty())
                    continue;
                String original = Paths.get(f.getOriginalFilename()).getFileName().toString();
                String ext = original.lastIndexOf('.') >= 0 ? original.substring(original.lastIndexOf('.')) : "";
                String name = java.util.UUID.randomUUID() + ext;
                Path dest = uploadRoot.resolve(name);
                try (var in = f.getInputStream()) {
                    Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                }
                ImageRef ref = new ImageRef();
                ref.setUrl("/uploads/" + name);
                ref.setAlt(req.getTitle());
                list.add(ref);
            }
            ad.setImages(list);
        }

        adRepo.save(ad);
        return "redirect:/ad/" + ad.getId();
    }

    @GetMapping("/ad/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editForm(@PathVariable String id, Model model, Principal principal) {
        Ad ad = adRepo.findById(id).orElseThrow();
        if (!ad.getOwnerId().equals(principal.getName())) {
            throw new SecurityException("Non autorisé");
        }
        model.addAttribute("ad", AdMapper.toRequest(ad));
        model.addAttribute("images", ad.getImages());
        return "ad-form";
    }

    @PostMapping("/ad/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public String update(@PathVariable String id,
            @Valid @ModelAttribute("ad") AdRequest req,
            @RequestParam(name = "files", required = false) List<MultipartFile> files,
            Principal principal) throws IOException {
        Ad ad = adRepo.findById(id).orElseThrow();
        if (!ad.getOwnerId().equals(principal.getName())) {
            throw new SecurityException("Non autorisé");
        }

        Ad updated = AdMapper.toEntity(req, principal.getName());
        updated.setId(ad.getId());
        updated.setImages(ad.getImages());

        if (files != null && !files.isEmpty()) {
            for (MultipartFile f : files) {
                if (f.isEmpty())
                    continue;
                String original = Paths.get(f.getOriginalFilename()).getFileName().toString();
                String ext = original.lastIndexOf('.') >= 0 ? original.substring(original.lastIndexOf('.')) : "";
                String name = java.util.UUID.randomUUID() + ext;
                Path dest = uploadRoot.resolve(name);
                try (var in = f.getInputStream()) {
                    Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                }
                ImageRef ref = new ImageRef();
                ref.setUrl("/uploads/" + name);
                ref.setAlt(req.getTitle());
                updated.getImages().add(ref);
            }
        }

        adRepo.save(updated);
        return "redirect:/ad/" + ad.getId();
    }

    @PostMapping("/ad/{adId}/image/{imageIndex}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteImage(@PathVariable String adId,
            @PathVariable int imageIndex,
            Principal principal) {
        Ad ad = adRepo.findById(adId).orElseThrow();
        if (!ad.getOwnerId().equals(principal.getName())) {
            throw new SecurityException("Non autorisé");
        }
        if (ad.getImages() != null && imageIndex >= 0 && imageIndex < ad.getImages().size()) {
            ad.getImages().remove(imageIndex);
            adRepo.save(ad);
        }
        return "redirect:/ad/edit/" + adId;
    }

//    @PostMapping("/ad/delete/{id}")
//    @PreAuthorize("isAuthenticated()")
//    public String delete(@PathVariable String id, Principal principal) {
//        Ad ad = adRepo.findById(id).orElseThrow();
//        if (!ad.getOwnerId().equals(principal.getName())) {
//            throw new SecurityException("Non autorisé");
//        }
//        adRepo.deleteById(id);
//        return "redirect:/profile";
//    }

    @PostMapping("/ad/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
        Ad ad = adRepo.findById(id).orElseThrow();
        if (!ad.getOwnerId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        adRepo.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
