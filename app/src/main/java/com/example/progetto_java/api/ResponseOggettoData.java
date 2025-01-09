package com.example.progetto_java.api;

public class ResponseOggettoData {

    private int id;
    private String name;
    private String type;
    private int level;
    private double lat;
    private double lon;
    private String image;

    // Costruttore
    public ResponseOggettoData() {
    }

    // Getter per id
    public int getId() {
        return id;
    }

    // Getter per name
    public String getName() {
        return name;
    }

    // Getter per type
    public String getType() {
        return type;
    }

    // Getter per level
    public int getLevel() {
        return level;
    }

    // Getter per lat
    public double getLat() {
        return lat;
    }

    // Getter per lon
    public double getLon() {
        return lon;
    }

    // Getter per image
    public String getImage() {
        return image;
    }
}

