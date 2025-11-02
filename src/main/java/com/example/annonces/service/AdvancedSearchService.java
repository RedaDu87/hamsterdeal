package com.example.annonces.service;

import com.example.annonces.domain.Ad;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class AdvancedSearchService {

    private final MongoTemplate mongo;

    public AdvancedSearchService(MongoTemplate mongo){ this.mongo = mongo; }

    public Page<Ad> search(Integer page, Integer size, Map<String, Object> filters){
        int p = page == null ? 0 : page;
        int s = size == null ? 12 : size;

        Criteria c = Criteria.where("active").is(true);
        List<Criteria> ands = new ArrayList<>();

        // texte libre (titre + description, regex insensible)
        String q = (String) filters.get("q");
        if (q != null && !q.isBlank()){
            String rx = ".*" + Pattern.quote(q).replace("\\ ", ".*") + ".*";
            ands.add(new Criteria().orOperator(
                    Criteria.where("title").regex(rx, "i"),
                    Criteria.where("description").regex(rx, "i")
            ));
        }

        // filtres simples
        ofStr(filters,"category").ifPresent(v -> ands.add(Criteria.where("category").is(v)));
        ofStr(filters,"canton").ifPresent(v -> ands.add(Criteria.where("canton").is(v)));

        // plages de prix
        ofNum(filters,"priceMin").ifPresent(v -> ands.add(Criteria.where("price").gte(v)));
        ofNum(filters,"priceMax").ifPresent(v -> ands.add(Criteria.where("price").lte(v)));

        // exemples par catégorie
        ofStr(filters,"car.make").ifPresent(v -> ands.add(Criteria.where("car.make").is(v)));
        ofNum(filters,"car.yearMin").ifPresent(v -> ands.add(Criteria.where("car.year").gte(v)));
        ofNum(filters,"car.yearMax").ifPresent(v -> ands.add(Criteria.where("car.year").lte(v)));
        ofNum(filters,"car.mileageMax").ifPresent(v -> ands.add(Criteria.where("car.mileage").lte(v)));

        ofStr(filters,"realestate.propertyType").ifPresent(v -> ands.add(Criteria.where("realestate.propertyType").is(v)));
        ofNum(filters,"realestate.roomsMin").ifPresent(v -> ands.add(Criteria.where("realestate.rooms").gte(v)));

        if (!ands.isEmpty()) c = new Criteria().andOperator(c).andOperator(ands.toArray(new Criteria[0]));

        Query qy = new Query(c)
                .with(PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt")));

        long total = mongo.count(qy, Ad.class);
        List<Ad> content = mongo.find(qy, Ad.class);
        return new PageImpl<>(content, PageRequest.of(p, s), total);
    }

    private static Optional<String> ofStr(Map<String,Object> m, String k){
        Object v = m.get(k);
        return (v instanceof String s && !s.isBlank()) ? Optional.of(s) : Optional.empty();
    }
    private static Optional<Number> ofNum(Map<String,Object> m, String k){
        Object v = m.get(k);
        return (v instanceof Number n) ? Optional.of(n) : Optional.empty();
    }
}
