package com.example.progetto_java.model;
import androidx.room.*;

    @Entity
    public class Utente {
        @PrimaryKey
        public int uid;

        @ColumnInfo(name = "name")
        public String name;

        @ColumnInfo(name = "picture")
        public String picture;

        @ColumnInfo(name = "life")
        public int life;

        @ColumnInfo(name = "experience")
        public int experience;

        @ColumnInfo(name = "weapon")
        public int weapon;

        @ColumnInfo(name = "armor")
        public int armor;

        @ColumnInfo(name = "amulet")
        public int amulet;

        @ColumnInfo(name = "profile_version")
        public int profileVersion;

        @ColumnInfo(name = "position_share")
        public boolean positionShare;

        @ColumnInfo(name = "lat")
        public double lat;

        @ColumnInfo(name = "lon")
        public double lon;

        public Utente(int uid, String name,String picture, int life, int experience,
                             int profileVersion, boolean positionShare) {
            this.uid = uid;
            this.name = name;
            this.life = life;
            this.experience = experience;
            this.picture = picture;
            this.profileVersion = profileVersion;
            this.positionShare = positionShare;
            amulet=0;
            armor=0;
            weapon=0;

        }

        @Override
        public String toString() {
            return "Utente{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", life=" + life +
                    ", experience=" + experience +
                    ", weapon='" + weapon + '\'' +
                    ", armor='" + armor + '\'' +
                    ", amulet='" + amulet + '\'' +
                    ", profileVersion=" + profileVersion +
                    ", positionShare=" + positionShare +
                    ", lat=" + lat +
                    ", lon=" + lon +
                    '}';
        }
        // Metodi getter
        public int getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public String getPicture() {
            return picture;
        }

        public int getLife() {
            return life;
        }

        public int getExperience() {
            return experience;
        }

        public int getWeapon() {
            return weapon;
        }

        public int getArmor() {
            return armor;
        }

        public int getAmulet() {
            return amulet;
        }

        public int getProfileVersion() {
            return profileVersion;
        }

        public boolean isShareLocation() {
            return positionShare;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        // Metodi setter
        public void setUid(int uid) {
            this.uid = uid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public void setLife(int life) {
            this.life = life;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public void setWeapon(int weapon) {
            this.weapon = weapon;
        }

        public void setArmor(int armor) {
            this.armor = armor;
        }

        public void setAmulet(int amulet) {
            this.amulet = amulet;
        }

        public void setProfileVersion(int profileVersion) {
            this.profileVersion = profileVersion;
        }

        public void setShareLocation(boolean shareLocation) {
            this.positionShare = shareLocation;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }
