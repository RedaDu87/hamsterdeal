package com.example.annonces.service;// AdService.java

import com.example.annonces.domain.Ad;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdService {

    private final MongoTemplate mongo;

    public AdService(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public Page<Ad> list(Integer page, Integer size,
            String q, String category, String canton,
            BigDecimal priceMin, BigDecimal priceMax) {

        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 12 : size);

        List<Criteria> ands = new ArrayList<>();
        ands.add(Criteria.where("active").is(true));

        if (q != null && !q.isBlank()) {
            // recherche titre + description, insensible à la casse
            ands.add(new Criteria().orOperator(
                    Criteria.where("title").regex(q, "i"),
                    Criteria.where("description").regex(q, "i")));
        }
        if (category != null && !category.isBlank()) {
            ands.add(Criteria.where("category").is(category));
        }
        if (canton != null && !canton.isBlank()) {
            ands.add(Criteria.where("canton").is(canton));
        }
        if (priceMin != null && priceMax != null) {
            // min ET max
            ands.add(
                    new Criteria().andOperator(
                            Criteria.where("price").gte(priceMin),
                            Criteria.where("price").lte(priceMax)));
        } else if (priceMin != null) {
            ands.add(Criteria.where("price").gte(priceMin));
        } else if (priceMax != null) {
            ands.add(Criteria.where("price").lte(priceMax));
        }

        Query query = new Query(new Criteria().andOperator(ands.toArray(new Criteria[0])));
        long total = mongo.count(query, Ad.class);
        query.with(pageable);

        List<Ad> items = mongo.find(query, Ad.class);
        return new PageImpl<>(items, pageable, total);
    }

    public Page<Ad> list(Integer page, Integer size,
            String q, String category, String canton,
            BigDecimal priceMin, BigDecimal priceMax,
            String sort, String dir) {

        int pageNo = page == null ? 0 : page;
        int pageSize = size == null ? 12 : size;

        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = (sort == null || sort.isBlank()) ? "createdAt" : sort;

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortField));

        List<Criteria> ands = new ArrayList<>();
        ands.add(Criteria.where("active").is(true));

        if (q != null && !q.isBlank()) {
            ands.add(new Criteria().orOperator(
                    Criteria.where("title").regex(q, "i"),
                    Criteria.where("description").regex(q, "i")));
        }
        if (category != null && !category.isBlank()) {
            ands.add(Criteria.where("category").is(category));
        }
        if (canton != null && !canton.isBlank()) {
            ands.add(Criteria.where("canton").is(canton));
        }
        if (priceMin != null && priceMax != null) {
            ands.add(new Criteria().andOperator(
                    Criteria.where("price").gte(priceMin),
                    Criteria.where("price").lte(priceMax)));
        } else if (priceMin != null) {
            ands.add(Criteria.where("price").gte(priceMin));
        } else if (priceMax != null) {
            ands.add(Criteria.where("price").lte(priceMax));
        }

        Query query = new Query(new Criteria().andOperator(ands.toArray(new Criteria[0])));
        long total = mongo.count(query, Ad.class);
        query.with(Sort.by(direction, sortField));
        query.with(pageable);
        List<Ad> items = mongo.find(query, Ad.class);

        return new PageImpl<>(items, pageable, total);
    }

}
