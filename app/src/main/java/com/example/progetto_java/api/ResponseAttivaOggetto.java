package com.example.progetto_java.api;

public class ResponseAttivaOggetto {
    private boolean died;
    private int life;
    private int experience;
    private Integer  weapon;
    private Integer  armor;
    private Integer amulet;

    public ResponseAttivaOggetto() {
    }

    public boolean isDied() {
        return died;
    }

    public int getLife() {
        return life;
    }

    public int getExperience() {
        return experience;
    }

    public Integer  getWeapon() {
        return weapon;
    }

    public Integer  getArmor() {
        return armor;
    }

    public Integer  getAmulet() {
        return amulet;
    }
}

