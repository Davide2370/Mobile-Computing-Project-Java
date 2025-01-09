package com.example.progetto_java.api;

public class ResponseRanking {
    private int uid;
    private int life;
    private int experience;
    private int profileversion;
    private boolean positionshare;
    private double lat;
    private double lon;

    // Costruttore
    public ResponseRanking() {
    }

    // Getter per UID
    public int getUid() {
        return uid;
    }

    // Getter per Life
    public int getLife() {
        return life;
    }

    // Getter per Experience
    public int getExperience() {
        return experience;
    }

    // Getter per ProfileVersion
    public int getProfileVersion() {
        return profileversion;
    }

    // Getter per PositionShare
    public boolean isPositionShare() {
        return positionshare;
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
