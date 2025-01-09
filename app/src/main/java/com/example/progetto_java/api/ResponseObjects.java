package com.example.progetto_java.api;

public class ResponseObjects {

    private int id;
    private double lat;
    private double lon;
    private String type;

    // Costruttore
    public ResponseObjects() {
    }

    // Getter per UID
    public int getid() {
        return id;
    }

    // Getter per Life
    public String getType() {
        return type;
    }

    // Getter per Latitudine (Lat)
    public double getLat() {
        return lat;
    }

    // Getter per Longitudine (Lon)
    public double getLon() {
        return lon;
    }
}
