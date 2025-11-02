package com.example.annonces.repo;

import com.example.annonces.domain.Ad;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.*;

import java.util.List;

public interface AdRepository extends MongoRepository<Ad, String> {

    Page<Ad> findByActiveTrue(Pageable pageable);

    Page<Ad> findByActiveTrueAndCategoryIgnoreCase(String category, Pageable pageable);

    List<Ad> findByOwnerId(String ownerId);

    Page<Ad> findByOwnerId(String ownerId, Pageable pageable);

    // Recherche q sur title OR description
    @Query("{ 'active': true, $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    Page<Ad> searchActiveByQ(String regex, Pageable pageable);

    // Avec category
    @Query("{ 'active': true, 'category': { $regex: ?1, $options: 'i' }, $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    Page<Ad> searchActiveByQAndCategory(String regex, String category, Pageable pageable);

    // Avec canton
    @Query("{ 'active': true, 'canton':  { $regex: ?1, $options: 'i' }, $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    Page<Ad> searchActiveByQAndCanton(String regex, String canton, Pageable pageable);

    // Avec category + canton
    @Query("{ 'active': true, 'category': { $regex: ?1, $options: 'i' }, 'canton': { $regex: ?2, $options: 'i' }, $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    Page<Ad> searchActiveByQAndCategoryAndCanton(String regex, String category, String canton, Pageable pageable);

    // Sans q, seulement canton
    Page<Ad> findByActiveTrueAndCantonIgnoreCase(String canton, Pageable pageable);

    // Sans q, category + canton
    Page<Ad> findByActiveTrueAndCategoryIgnoreCaseAndCantonIgnoreCase(String category, String canton, Pageable pageable);

    @Query("{ 'active': true, " +
            "  $or: [ " +
            "    { 'title': { $regex: ?0, $options: 'i' } }, " +
            "    { 'description': { $regex: ?0, $options: 'i' } } " +
            "  ], " +
            "  'price': { $gte: ?1, $lte: ?2 } " +
            "}")
    Page<Ad> searchActiveByQAndPriceRange(String regex, double minPrice, double maxPrice, Pageable pageable);

    @Query("{ 'active': true, " +
            "  'createdAt': { $gte: ?0, $lte: ?1 } " +
            "}")
    Page<Ad> searchActiveByDateRange(java.time.Instant from, java.time.Instant to, Pageable pageable);

}
