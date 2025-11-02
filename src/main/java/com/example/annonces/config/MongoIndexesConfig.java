package com.example.annonces.config;

import com.example.annonces.domain.Ad;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
public class MongoIndexesConfig {

    @Bean
    public Object ensureIndexes(MongoTemplate mongoTemplate){
        IndexOperations ops = mongoTemplate.indexOps(Ad.class);

        // Champs transverses
        ops.ensureIndex(new Index().on("active", org.springframework.data.domain.Sort.Direction.DESC));
        ops.ensureIndex(new Index().on("category", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("canton", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("price", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.DESC));

        // Car (exemples)
        ops.ensureIndex(new Index().on("car.make", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("car.model", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("car.year", org.springframework.data.domain.Sort.Direction.DESC));
        ops.ensureIndex(new Index().on("car.mileage", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("car.fuel", org.springframework.data.domain.Sort.Direction.ASC));

        // RealEstate (exemples)
        ops.ensureIndex(new Index().on("realestate.propertyType", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("realestate.rooms", org.springframework.data.domain.Sort.Direction.DESC));
        ops.ensureIndex(new Index().on("realestate.area", org.springframework.data.domain.Sort.Direction.DESC));

        // Jobs (exemples)
        ops.ensureIndex(new Index().on("jobs.role", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("jobs.remote", org.springframework.data.domain.Sort.Direction.ASC));

        // Electronics (exemples)
        ops.ensureIndex(new Index().on("electronics.type", org.springframework.data.domain.Sort.Direction.ASC));
        ops.ensureIndex(new Index().on("electronics.ramGb", org.springframework.data.domain.Sort.Direction.DESC));
        ops.ensureIndex(new Index().on("electronics.storageGb", org.springframework.data.domain.Sort.Direction.DESC));

        return new Object();
    }
}
