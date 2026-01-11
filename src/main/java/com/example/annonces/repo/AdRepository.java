package com.example.annonces.repo;

import com.example.annonces.domain.Ad;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdRepository extends MongoRepository<Ad, String> {


}
