package com.example.annonces.npa.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "npa")
public class NpaDto {

    @Id
    private String id;

    @Indexed
    private Integer npa;              // PLZ4

    @Indexed
    private String ville;             // Ortschaftsname

    private Integer zusatzZiffer;     // ZusatzZiffer
    private Integer zipId;            // ZIP_ID
    private String commune;           // Gemeindename
    private Integer bfs;              // BFS-Nr

    @Indexed
    private String canton;            // Kantonskürzel

    private Double adressenant;       // %

    private String langue;            // fr / de / it
    private LocalDate validity;       // Validity

    /* ===== GETTERS / SETTERS ===== */

    public String getId() {
        return id;
    }

    public Integer getNpa() {
        return npa;
    }

    public void setNpa(Integer npa) {
        this.npa = npa;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Integer getZusatzZiffer() {
        return zusatzZiffer;
    }

    public void setZusatzZiffer(Integer zusatzZiffer) {
        this.zusatzZiffer = zusatzZiffer;
    }

    public Integer getZipId() {
        return zipId;
    }

    public void setZipId(Integer zipId) {
        this.zipId = zipId;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public Integer getBfs() {
        return bfs;
    }

    public void setBfs(Integer bfs) {
        this.bfs = bfs;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public Double getAdressenant() {
        return adressenant;
    }

    public void setAdressenant(Double adressenant) {
        this.adressenant = adressenant;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }
}
