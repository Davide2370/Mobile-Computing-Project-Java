package com.example.progetto_java.api;

public class ResponseUtentiVicini {

    private int uid;
    private double lat;
    private double lon;
    private int profileversion;
    private int life;
    private int experience;
    private String time;

    // Costruttore
    public ResponseUtentiVicini() {
    }

    // Getter per UID
    public int getUid() {
        return uid;
    }

    // Getter per Latitudine (Lat)
    public double getLat() {
        return lat;
    }

    // Getter per Longitudine (Lon)
    public double getLon() {
        return lon;
    }

    // Getter per Versione del Profilo (Profile Version)
    public int getProfileVersion() {
        return profileversion;
    }

    // Getter per Life
    public int getLife() {
        return life;
    }

    // Getter per Esperienza (Experience)
    public int getExperience() {
        return experience;
    }

    // Getter per Time
    public String getTime() {
        return time;
    }
}
