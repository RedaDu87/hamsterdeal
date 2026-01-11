package com.example.annonces.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AdRequest {

    private String id;

    // Commun
    @NotBlank private String title;
    @NotBlank private String description;
    @NotNull @DecimalMin("0.0") private BigDecimal price;
    @NotBlank private String category;     // car, electronics, ...
    @NotBlank private String canton;       // ZH, GE, ...
    private String condition;              // new, like_new, good, fair, for_parts
    private String brand;
    private String color;
    @NotNull
    @Min(1000)
    @Max(9999)
    private Integer npa;
    private String city;

    // Catégories (optionnelles)
    @Valid private CarDTO car;
    @Valid private ElectronicsDTO electronics;
    @Valid private HomeDTO home;
    @Valid private FashionDTO fashion;
    @Valid private SportsDTO sports;
    @Valid private ToysDTO toys;
    @Valid private BooksDTO books;
    @Valid private MusicDTO music;
    @Valid private ToolsDTO tools;
    @Valid private GardenDTO garden;
    @Valid private PetsDTO pets;
    @Valid private JobsDTO jobs;
    @Valid private ServicesDTO services;
    @Valid private RealEstateDTO realestate;
    @Valid private CollectiblesDTO collectibles;

    /* ===== Sous-DTO par catégorie ===== */
    @Setter
    @Getter
    public static class CarDTO {
        private String make;
        private String model;
        private Integer year;
        private Integer mileage;
        private String fuel;
        private String transmission;
        private String bodyType;
        private Integer doors;
        private Integer seats;
        private Integer powerHp;
        private String drivetrain;
        private String vin;
        // getters/setters
        // ...
    }
    @Setter
    @Getter
    public static class ElectronicsDTO {

        @NotBlank
        @Pattern(
                regexp = "phone|laptop|tv|console|tablet|smartwatch|camera",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Invalid electronics type"
        )
        private String type;

        private String model;
    }
    @Setter
    @Getter
    public static class HomeDTO {
        private String subcategory; // appliance, decor...
        private String material;
        private String dimensions;
        private Boolean assembled;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class FashionDTO {
        private String gender;    // men, women, unisex, kids
        private String size;
        private String material;
        private String style;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class SportsDTO {
        private String sport;     // football, ski...
        private String equipment; // shoes, racket...
        private String size;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class ToysDTO {
        private String ageRange;  // 0-2, 3-5...
        private String material;
        private Boolean educational;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class BooksDTO {
        private String author;
        private String language;
        private String format;    // paperback, hardcover
        private String isbn;
        private String genre;
        private Integer year;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class MusicDTO {
        private String media;     // vinyl, cd...
        private String artist;
        private String album;
        private String genre;
        private Integer year;
        private String instrument;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class ToolsDTO {
        private String type;      // drill, saw...
        private String power;     // corded, battery
        private Integer voltage;
        private Boolean proGrade;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class GardenDTO {
        private String type;      // mower, plant...
        private String season;    // spring, summer...
        private Boolean motorized;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class PetsDTO {
        private String species;   // dog, cat...
        private String breed;
        private Integer ageMonths;
        private String sex;       // m, f
        private Boolean vaccinated;
        private Boolean microchipped;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class JobsDTO {
        private String role;
        private String company;
        private String contractType;  // full_time...
        private String experience;    // junior/mid/senior
        private Boolean remote;
        private BigDecimal salaryMin;
        private BigDecimal salaryMax;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class ServicesDTO {
        private String serviceType;   // cleaning...
        private String availability;  // weekdays...
        private Boolean professional;
        private BigDecimal hourlyRate;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class RealEstateDTO {
        private String propertyType;  // apartment, house...
        private BigDecimal area;      // m²
        private Integer rooms;
        private Integer bathrooms;
        private Boolean furnished;
        private Boolean parking;
        private Integer floor;
        private Integer yearBuilt;
        // getters/setters...
    }
    @Setter
    @Getter
    public static class CollectiblesDTO {
        private String collectibleType; // cards, coins...
        private String era;             // vintage, modern...
        private Boolean certified;
        // getters/setters...
    }

    /* getters/setters des champs du AdRequest (communs + nested) */
    // ...
}
