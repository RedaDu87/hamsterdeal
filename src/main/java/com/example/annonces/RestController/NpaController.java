package com.example.annonces.RestController;

import com.example.annonces.repo.NpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/npa")
public class NpaController {

    private final NpaRepository npaRepository;

    public NpaController(NpaRepository npaRepository) {
        this.npaRepository = npaRepository;
    }

    @GetMapping("/search")
    public List<com.example.annonces.npa.dto.NpaDto> search(@RequestParam("q") String q) {

        // NPA
        if (q.matches("\\d{2,4}")) {
            try {
                int npa = Integer.parseInt(q);
                return npaRepository.findByNpa(npa);
            } catch (NumberFormatException e) {
                return List.of();
            }
        }

        // Ville
        return npaRepository.findTop10ByVilleStartingWithIgnoreCase(q);
    }
}


