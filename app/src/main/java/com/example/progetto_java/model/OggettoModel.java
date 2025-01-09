package com.example.progetto_java.model;

import androidx.room.*;

//questo è effettivamente il database
@Database(entities = {Oggetto.class}, version = 2)
public abstract class OggettoModel extends RoomDatabase {
    public abstract OggettoDao oggettoDao();
}
