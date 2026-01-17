package com.example.annonces.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class SwissNpaCsvImporter {

    private static final String CSV_PATH = "AMTOVZ_CSV_LV95.csv";

    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "annonces";
    private static final String COLLECTION = "npa";

    public static void main(String[] args) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (
                MongoClient mongoClient = MongoClients.create(MONGO_URI);
                InputStream is = SwissNpaCsvImporter.class
                        .getClassLoader()
                        .getResourceAsStream(CSV_PATH);
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
        ) {
            if (is == null) {
                throw new IllegalStateException("CSV introuvable : " + CSV_PATH);
            }


            MongoDatabase db = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = db.getCollection(COLLECTION);

            String line;
            boolean firstLine = true;
            int count = 0;

            while ((line = br.readLine()) != null) {

                // skip header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] cols = line.split(";", -1);

                Document doc = new Document()
                        .append("ville", cols[0])
                        .append("npa", parseInt(cols[1]))
                        .append("zusatzZiffer", parseInt(cols[2]))
                        .append("zipId", parseInt(cols[3]))
                        .append("commune", cols[4])
                        .append("bfs", parseInt(cols[5]))
                        .append("canton", cols[6])
                        .append("adressenant", parsePercent(cols[7]))
                        .append("langue", cols[10])
                        .append("validity", parseDate(cols[11], dateFormatter));

                collection.insertOne(doc);
                count++;
            }

            System.out.println("✅ Import terminé : " + count + " lignes insérées");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Integer parseInt(String v) {
        if (v == null || v.isBlank()) return null;
        return Integer.parseInt(v.trim());
    }

    private static Double parsePercent(String v) {
        if (v == null || v.isBlank()) return null;
        return Double.parseDouble(v.replace("%", "").trim());
    }

    private static LocalDate parseDate(String v, DateTimeFormatter fmt) {
        if (v == null || v.isBlank()) return null;
        return LocalDate.parse(v.trim(), fmt);
    }
}

