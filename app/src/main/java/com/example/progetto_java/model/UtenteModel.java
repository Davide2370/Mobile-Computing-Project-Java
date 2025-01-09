package com.example.progetto_java.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Utente.class}, version = 2)
public abstract class UtenteModel extends RoomDatabase {
    public abstract UtenteDao utenteDao();
}
