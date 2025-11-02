package com.example.annonces.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/geo")
public class GeocodingController {

    @GetMapping("/coords")
    public Map<String, Double> getCoordinates(@RequestParam String city,
            @RequestParam(required = false) String canton) {
        String query = city + (canton != null ? ", " + canton : ", Suisse");
        String url = "https://nominatim.openstreetmap.org/search?format=json&q="
                + URLEncoder.encode(query, StandardCharsets.UTF_8);

        try {
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "AnnonceApp/1.0 (reda.berkouch@outlook.com)") // requis !
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var results = new ObjectMapper().readTree(response.body());
            if (results.size() > 0) {
                return Map.of(
                        "lat", results.get(0).get("lat").asDouble(),
                        "lon", results.get(0).get("lon").asDouble());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of();
    }
}