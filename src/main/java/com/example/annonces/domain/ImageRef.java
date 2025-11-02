package com.example.annonces.domain;

public class ImageRef {
    private String url; // ex: "/uploads/xxxx.jpg"
    private String alt;
    // getters/setters


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
