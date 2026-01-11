package com.example.annonces.domain;

public enum ElectronicsType {
    PHONE,
    LAPTOP,
    TV,
    CONSOLE,
    TABLET,
    SMARTWATCH,
    CAMERA;

    public static ElectronicsType from(String value) {
        if (value == null) return null;
        return ElectronicsType.valueOf(value.toUpperCase());
    }
}


