# 🐹 Hamstereal

**Hamstereal** est une application web backend-driven construite avec **Spring Boot**, **Thymeleaf** et **MongoDB**.  
Elle repose sur une architecture MVC classique, sécurisée et prête pour un usage professionnel ou un déploiement en production.

---

## 🚀 Fonctionnalités principales

- 🌐 Application web server-side avec Thymeleaf
- 🔐 Authentification sécurisée avec Spring Security
- 👤 Gestion des utilisateurs
- 🧠 Architecture MVC claire (Controller / Service / Repository)
- 🗄️ Base de données NoSQL MongoDB
- 🌍 Internationalisation (i18n)
- ⚙️ Configuration dev / prod
- 🧪 Prête pour tests et CI/CD

---

## 🧱 Architecture

hamstereal/
│
├── src/main/java
│ └── ch/hamstereal/
│ ├── controller/ # Contrôleurs MVC
│ ├── service/ # Logique métier
│ ├── repository/ # Accès MongoDB
│ ├── security/ # Spring Security
│ └── model/ # Entités & DTO
│
├── src/main/resources
│ ├── templates/ # Thymeleaf (.html)
│ ├── static/ # CSS / JS / images
│ ├── messages/ # i18n
│ └── application.yml
│
└── pom.xml


---

## 🛠️ Stack technique

### Backend / Web
- Java 17+
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring Security

### Base de données
- MongoDB
- Spring Data MongoDB

### Build & DevOps
- Maven
- Docker (optionnel)
- GitHub Actions (optionnel)

---

## 🔐 Sécurité

- Configuration **Spring Security personnalisée**
- `SecurityFilterChain` explicite
- Pas de user/password par défaut Spring Boot
- Support JWT ou session (selon configuration)
- Protection CSRF configurable

---

## 🗄️ MongoDB

Connexion locale par défaut :

mongodb://localhost:27017/hamstereal

▶️ Lancer le projet en local
Prérequis

Java 17+

Maven

MongoDB en local

Démarrage

http://localhost:8080

spring:
data:
mongodb:
uri: mongodb://localhost:27017/hamstereal

thymeleaf:
cache: false

server:
port: 8080

🧪 Tests

Tests unitaires avec JUnit 5

Mockito pour les services

Tests MVC possibles avec @WebMvcTest

📌 Roadmap

Gestion des rôles (ADMIN / USER)

OAuth2 (Google / GitHub)

Mode full stateless (JWT)

Pagination & filtres

Monitoring (Spring Actuator)

👨‍💻 Auteur

Réda Berkouch
Senior Java Engineer – Spring / Web / Sécurité
📍 Suisse (VD)