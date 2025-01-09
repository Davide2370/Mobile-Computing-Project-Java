package com.example.progetto_java.model;
import androidx.room.*;

    @Entity
    public class Oggetto {

        @PrimaryKey
        public int id;
        public String type;
        public int level;
        public double lat;
        public double lon;
        public String image;
        public String name;
        public int distanza;

        public boolean isNear;

        // Costruttore vuoto necessario per Room
        public Oggetto() {
        }

        // Costruttore pieno
        public Oggetto(int id, String type, int level, String image, String name) {
            this.id = id;
            this.type = type;
            this.level = level;
            this.image = image;
            this.name = name;
        }

        // Metodi getter
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public int getLevel() {
            return level;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public String getImage() {
            return image;
        }

        public boolean getIsNear() {
            return isNear;
        }
        public void setIsNear(boolean isNear) {
            this.isNear = isNear;
        }

        public int getDistance() {
            return distanza;
        }

        public void setDistance(int distanza) {this.distanza =distanza;}

        // Metodi setter
        public void setId(int id) {
            this.id = id;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public void setImage(String image) {
            this.image = image;
        }

}
