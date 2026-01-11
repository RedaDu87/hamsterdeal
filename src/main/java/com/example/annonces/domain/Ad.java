package com.example.annonces.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Document("ads")
public class Ad {
    @Id
    private String id;

    @TextIndexed
    private String title;

    @TextIndexed
    private String description;

    @Indexed
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    @Indexed
    private String category; // car, electronics, ...

    @Indexed
    private String canton; // ZH, GE, VD, ...

    @Indexed
    private String ownerId;
    private Instant createdAt = Instant.now();
    private List<ImageRef> images;
    private boolean active = true;

    // ATTRIBUTS TRANSVERSES
    private String condition; // new, like_new, good, fair, for_parts
    private String brand; // marque générique (si pertinent)
    private String color; // couleur générique (si pertinent)
    @Indexed
    private Integer npa;

    private String city; // ville (utile pour la recherche)

    // BLOCS PAR CATÉGORIE (tous optionnels)
    @Field("car")
    private CarAttrs car;
    @Field("electronics")
    private ElectronicsAttrs electronics;
    @Field("home")
    private HomeAttrs home;
    @Field("fashion")
    private FashionAttrs fashion;
    @Field("sports")
    private SportsAttrs sports;
    @Field("toys")
    private ToysAttrs toys;
    @Field("books")
    private BooksAttrs books;
    @Field("music")
    private MusicAttrs music;
    @Field("tools")
    private ToolsAttrs tools;
    @Field("garden")
    private GardenAttrs garden;
    @Field("pets")
    private PetsAttrs pets;
    @Field("jobs")
    private JobsAttrs jobs;
    @Field("services")
    private ServicesAttrs services;
    @Field("realestate")
    private RealEstateAttrs realestate;
    @Field("collectibles")
    private CollectiblesAttrs collectibles;

    // Pour cas particuliers
    private Map<String, Object> extra;

    /* ====== ATTRS CLASSES ====== */
    @Setter
    @Getter
    public static class CarAttrs {
        private String make; // marque (BMW, Toyota…)
        private String model; // modèle
        private Integer year; // année
        private Integer mileage; // km
        private String fuel; // petrol, diesel, hybrid, electric
        private String transmission; // manual, auto
        private String bodyType; // hatchback, sedan, suv...
        private Integer doors; // 2, 3, 4, 5
        private Integer seats; // 2..9
        private Integer powerHp; // chevaux
        private String drivetrain; // fwd, rwd, awd
        private String vin; // optionnel
        // getters/setters...
        // (omets ici pour la brièveté)

    }

    @Setter
    @Getter
    public static class ElectronicsAttrs {
        private ElectronicsType type; // phone, laptop, tv, console…
        private String model;

    }

    @Setter
    @Getter
    public static class HomeAttrs {
        private String subcategory; // appliance, decor, kitchen, bedding…
        private String material; // wood, metal, glass…
        private String dimensions; // LxWxH
        private Boolean assembled; // monté ?
        // getters/setters...

    }

    @Setter
    @Getter
    public static class FashionAttrs {
        private String gender; // men, women, unisex, kids
        private String size; // S, 40, EU43…
        private String material; // cotton, leather…
        private String style; // street, formal…
        // getters/setters...

    }

    @Setter
    @Getter
    public static class SportsAttrs {
        private String sport; // football, ski, cycling…
        private String equipment; // shoes, racket, bike…
        private String size; // taille équipement
        // getters/setters...

    }

    @Setter
    @Getter
    public static class ToysAttrs {
        private String ageRange; // 0-2, 3-5, 6-8, 9-12, 12+
        private String material; // plastic, wood…
        private Boolean educational;
        // getters/setters...

    }

    @Setter
    @Getter
    public static class BooksAttrs {
        private String author;
        private String language; // fr, en, de, it…
        private String format; // paperback, hardcover
        private String isbn;
        private String genre; // roman, sf, bd…
        private Integer year;
        // getters/setters...

    }

    @Setter
    @Getter
    public static class MusicAttrs {
        private String media; // vinyl, cd, cassette, digital
        private String artist;
        private String album;
        private String genre; // rock, jazz…
        private Integer year;
        private String instrument; // si instrument en vente
        // getters/setters...

    }

    @Setter
    @Getter
    public static class ToolsAttrs {
        private String type; // drill, saw…
        private String power; // corded, battery
        private Integer voltage; // 12, 18, 220…
        private Boolean proGrade; // gamme pro
        // getters/setters...

    }

    @Setter
    @Getter
    public static class GardenAttrs {
        private String type; // mower, plant, soil, furniture…
        private String season; // spring, summer…
        private Boolean motorized;
        // getters/setters...

    }

    @Setter
    @Getter
    public static class PetsAttrs {
        private String species; // dog, cat, fish…
        private String breed;
        private Integer ageMonths;
        private String sex; // m, f
        private Boolean vaccinated;
        private Boolean microchipped;
        // getters/setters...

    }

    @Setter
    @Getter
    public static class JobsAttrs {
        private String role;
        private String company;
        private String contractType; // full_time, part_time, temp, internship
        private String experience; // junior, mid, senior
        private Boolean remote; // télétravail possible ?
        private BigDecimal salaryMin;
        private BigDecimal salaryMax;
        // getters/setters...

    }

    @Setter
    @Getter
    public static class ServicesAttrs {
        private String serviceType; // cleaning, moving, tutoring…
        private String availability; // weekdays, weekend, evenings…
        private Boolean professional; // entreprise déclarée ?
        private BigDecimal hourlyRate;
        // getters/setters...

    }

    @Setter
    @Getter
    public static class RealEstateAttrs {
        private String propertyType; // apartment, house, room, office…
        private BigDecimal area; // m²
        private Integer rooms; // pièces
        private Integer bathrooms;
        private Boolean furnished;
        private Boolean parking;
        private Integer floor;
        private Integer yearBuilt;
        // getters/setters...

    }

    @Setter
    @Getter
    public static class CollectiblesAttrs {
        private String collectibleType;// cards, coins, stamps, art…
        private String era; // vintage, modern…
        private Boolean certified; // certificat authenticité
        // getters/setters...

    }

}
