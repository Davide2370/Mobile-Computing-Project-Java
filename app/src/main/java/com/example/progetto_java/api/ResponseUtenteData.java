package com.example.progetto_java.api;

    public class ResponseUtenteData {
        private int uid;
        private String name;
        private int life;
        private int experience;
        private int weapon;
        private int armor;
        private int amulet;
        private String picture;
        private int profileversion;
        private boolean positionshare;

        // Costruttore
        public ResponseUtenteData() {
        }

        // Getter per UID
        public int getUid() {
            return uid;
        }

        // Getter per Name
        public String getName() {
            return name;
        }

        // Getter per Life
        public int getLife() {
            return life;
        }

        // Getter per Experience
        public int getExperience() {
            return experience;
        }

        // Getter per Weapon
        public int getWeapon() {
            return weapon;
        }

        // Getter per Armor
        public int getArmor() {
            return armor;
        }

        // Getter per Amulet
        public int getAmulet() {
            return amulet;
        }

        // Getter per Picture
        public String getPicture() {
            return picture;
        }

        // Getter per ProfileVersion
        public int getProfileVersion() {
            return profileversion;
        }

        // Getter per PositionShare
        public boolean isPositionShare() {
            return positionshare;
        }

    }
