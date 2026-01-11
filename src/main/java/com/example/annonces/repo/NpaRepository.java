package com.example.annonces.repo;

import com.example.annonces.npa.dto.NpaDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NpaRepository extends MongoRepository<NpaDto, String> {

    // 🔎 Recherche par NPA
    List<NpaDto> findByNpa(Integer npa);

    // 🔎 Recherche par ville (case-insensitive)
    List<NpaDto> findByVilleIgnoreCase(String ville);

    // 🔎 Autocomplete ville
    List<NpaDto> findTop10ByVilleStartingWithIgnoreCase(String prefix);

    // 🔎 Ville + NPA
    Optional<NpaDto> findFirstByVilleIgnoreCaseAndNpa(String ville, Integer npa);

    // 🔎 Par canton
    List<NpaDto> findByCanton(String canton);
}
